package com.globus.claim_service.service.impl;

import com.globus.claim_service.dto.*;
import com.globus.claim_service.dto.credit.CreditClaimRequest;
import com.globus.claim_service.dto.criteria.ClaimSearchCriteria;
import com.globus.claim_service.dto.customer.CustomerProfileDto;
import com.globus.claim_service.event.*;
import com.globus.claim_service.feign.CustomerFeignClient;
import com.globus.claim_service.mapper.ClaimMapper;
import com.globus.claim_service.mapper.LoanAppEventMapper;
import com.globus.claim_service.mapper.NotificationMapper;
import com.globus.claim_service.model.Claim;
import com.globus.claim_service.model.enums.ClaimStatus;
import com.globus.claim_service.repository.ClaimRepository;
import com.globus.claim_service.service.ClaimService;
import com.globus.claim_service.service.kafka.producer.AccountProducerKafkaService;
import com.globus.claim_service.service.kafka.producer.LoanProducerKafkaService;
import com.globus.claim_service.service.kafka.producer.NotificationProducerKafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository repository;
    private final ClaimMapper claimMapper;
    private final LoanAppEventMapper eventMapper;
    private final LoanProducerKafkaService loanProducerKafkaService;
    private final CustomerFeignClient customerFeignClient;
    private final AccountProducerKafkaService accountProducerKafkaService;
    private final NotificationProducerKafkaService notificationProducerKafkaService;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    @Override
    public Claim getClaimById(UUID id) {
        log.info("Get Clime by Id {}", id.toString());
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageFormat.format("Claim with id: {0} not found", id))
                );
    }

    @Transactional(readOnly = true)
    @Override
    public List<Claim> getAllClaimsByFilter(int page, int size, ClaimFilter filter) {
        log.info("Get All Claims By Filter with amount of pages {}", page);
        return repository.findAll(ClaimSearchCriteria.withFilter(filter),
                PageRequest.of(
                        page,
                        size)).getContent();
    }

    @Override
    public void createCreditClaim(CreditClaimRequest claimRq) {
        log.info("create Credit Claim for Customer Id {}", claimRq.getCustomerId());
        CustomerProfileDto customer = customerFeignClient.getCustomerById(claimRq.getCustomerId());

        if (!"Active".equals(customer.getCustomerStatus())) {
            ClaimRejectedEvent rejectedEvent = new ClaimRejectedEvent();
            rejectedEvent.setCustomerId(claimRq.getCustomerId());
            rejectedEvent.setReason("CUSTOMER_STATUS_INACTIVE");
            rejectedEvent.setOriginalRequest(claimRq);
            rejectedEvent.setTimestamp(Instant.now());
            notificationProducerKafkaService.sendNotification(notificationMapper.toRejectedNotificationEvent(rejectedEvent));
        } else {
            Claim claim = claimMapper.toEntity(claimRq);
            claim = saveClaim(claim);

            LoanAppEvent event = eventMapper.toLoanAppEvent(claimRq, customer);
            event.setClaimId(claim.getClaimId());
            event.setStatus(ClaimStatus.PENDING);

            AccountRequest accountRequest = new AccountRequest(
                    claim.getClaimId().toString(),
                    claim.getCustomerId().toString(),
                    "open"
            );

            accountProducerKafkaService.sendAccountRequest(accountRequest);
            loanProducerKafkaService.sendCreditClaim(event);
            notificationProducerKafkaService.sendNotification(notificationMapper.toSuccessNotificationEvent(event));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void acceptCredit(UUID claimId) {
        log.info("Accept Credit with clime Id {}", claimId.toString());
        Claim claim = getClaimById(claimId);
        claim.setStatus(ClaimStatus.ACCEPTED);
        claim.setUpdatedAt(Instant.now());
        repository.save(claim);
        loanProducerKafkaService.sendAcceptCreditClaim(
                new LoanAcceptCreditClaim(
                        "Accept credit claim", claim.getClaimId(), claim.getStatus(), claim.getToBill()));
    }

    @Override
    public void deleteById(UUID claimId) {
        log.info("Delete By Id {}", claimId.toString());
        repository.deleteById(claimId);
    }

    @Transactional
    public Claim saveClaim(Claim claim) {
        return repository.save(claim);
    }
}

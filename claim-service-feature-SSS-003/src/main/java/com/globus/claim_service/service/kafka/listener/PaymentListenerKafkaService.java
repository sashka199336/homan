package com.globus.claim_service.service.kafka.listener;

import com.globus.claim_service.event.PaymentResponse;
import com.globus.claim_service.mapper.NotificationMapper;
import com.globus.claim_service.model.Claim;
import com.globus.claim_service.repository.ClaimRepository;
import com.globus.claim_service.service.kafka.producer.NotificationProducerKafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentListenerKafkaService {
    private final ClaimRepository claimRepository;
    private final NotificationProducerKafkaService notificationProducerKafkaService;
    private final NotificationMapper notificationMapper;

    @KafkaListener(topics = "${spring.kafka.topic.payment-events}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentResponse(PaymentResponse response) {
        log.info("Received Payment Service Response from topic payment.claim.direct-events with claimId {}", response.getClaimId());
        log.info("Received payment service response: {}", response);
        Optional<Claim> optClaim = claimRepository.findById(response.getClaimId());
        if (optClaim.isPresent()) {
            Claim claim = optClaim.get();
            claimRepository.updateStatus(response.getClaimId(), response.getClaimStatus().name(), Instant.now());
            notificationProducerKafkaService.sendNotification(notificationMapper.toPaymentCompletedMessage(response, claim));
        } else {
            log.atError()
                    .setCause(new ResourceNotFoundException(MessageFormat.format("Claim with id: {0} not found", response.getClaimId())))
                    .setMessage("From Payment Service received message [claimId:%s , claimStatus:%s ] with claimId, which not registered!!! Message ignored!!!}"
                            .formatted(response.getClaimId(), response.getClaimStatus()))
                    .log();
        }
    }
}

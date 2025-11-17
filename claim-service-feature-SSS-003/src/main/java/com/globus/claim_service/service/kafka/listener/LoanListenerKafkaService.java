package com.globus.claim_service.service.kafka.listener;

import com.globus.claim_service.dto.credit.CreditClaimResponse;
import com.globus.claim_service.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanListenerKafkaService {
    private final ClaimRepository claimRepository;

    @KafkaListener(topics = "${spring.kafka.consumer.topics.loan-claim}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleLoanResponse(CreditClaimResponse response) {
        log.info("Received Loan Response from topic loan.claim.decision with ClimeId {}", response.getClaimId());
        claimRepository.updateStatus(
                response.getClaimId(),
                response.getStatus(),
                Instant.now()
        );
        log.info("Received loan response: {}", response);
    }
}

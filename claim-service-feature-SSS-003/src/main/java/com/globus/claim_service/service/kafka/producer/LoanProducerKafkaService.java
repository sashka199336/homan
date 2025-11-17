package com.globus.claim_service.service.kafka.producer;

import com.globus.claim_service.event.LoanAcceptCreditClaim;
import com.globus.claim_service.event.LoanAppEvent;
import com.globus.claim_service.service.kafka.KafkaMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanProducerKafkaService {
    private final KafkaMessageService kafkaMessageService;

    @Value("${spring.kafka.topic.loan-created:claim.loan.created}")
    private String loanCreatedTopic;

    @Value("${spring.kafka.topic.loan-confirmed:claim.loan.confirmed}")
    private String loanConfirmedTopic;

    public void sendCreditClaim(LoanAppEvent event) {
        log.info("Sending Credit Claim message to topic {} with ClaimId {}", loanCreatedTopic, event.getClaimId().toString());
        kafkaMessageService.sendMessage(loanCreatedTopic, event.getClaimId().toString(), event);
    }

    public void sendAcceptCreditClaim(LoanAcceptCreditClaim event) {
        log.info("Sending Accept Credit Claim message to topic {} with ClaimId {}", loanConfirmedTopic, event.getClaimId().toString());
        kafkaMessageService.sendMessage(loanConfirmedTopic, event.getClaimId().toString(), event);
    }
}

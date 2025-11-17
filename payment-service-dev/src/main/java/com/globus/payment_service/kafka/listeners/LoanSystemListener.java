package com.globus.payment_service.kafka.listeners;

import com.globus.payment_service.dto.NeedForPaymentEventFromLoanSystem;
import com.globus.payment_service.service.interfaces.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanSystemListener {

    private final TransactionService transactionService;

    @KafkaListener(
            topics = "${LOAN_PAYMENT_DIRECT_TOPIC}",
            groupId = "payment-service"
    )
    public void listenLoanEventToTransfer(Message<NeedForPaymentEventFromLoanSystem> receivedMessage) {
        log.info("Received request from Loan Service with claim ID: {}", receivedMessage.getPayload().getClaimId());
        transactionService.startTransaction(receivedMessage.getPayload());
    }
}

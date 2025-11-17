package com.globus.payment_service.kafka.listeners;

import com.globus.payment_service.dto.FundsTransferCompletionEvent;
import com.globus.payment_service.service.interfaces.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountServiceListener {

    private final TransactionService transactionService;

    @KafkaListener(
            topics = "${ACCOUNT_PAYMENT_DIRECT_TOPIC}",
            groupId = "payment-service"
    )
    public void listenAccountResponseAsTransferResult(Message<FundsTransferCompletionEvent> receivedMessage) {
        log.info("Received response from Account Service with transaction ID: {}", receivedMessage.getPayload().getTransactionId());
        transactionService.finalizeTransaction(receivedMessage.getPayload());
    }
}

package com.globus.payment_service.kafka.producers;

import com.globus.payment_service.dto.DemandForPaymentEventToAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


@Component
@RequiredArgsConstructor
@Slf4j
public class AccountServiceProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${PAYMENT_ACCOUNT_DIRECT_TOPIC}")
    String topic;

    public CompletableFuture<Object> sendAccountServiceDataToTransfer(DemandForPaymentEventToAccountService request) {
        Message<DemandForPaymentEventToAccountService> messageToAccount = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        log.info("Request prepared with transaction ID: {}", request.getTransactionId());

        return kafkaTemplate.send(messageToAccount)
                .thenApply(sendResult -> {
                    log.info("Successful sending process in Account Service with transaction ID: {}", request.getTransactionId());
                    return null;
                })
                .exceptionally(ex -> {
                    log.error("Failed sending process in Account Service with transaction ID: {}", request.getTransactionId());
                    throw new CompletionException("Failed sending in Account Service", ex);
                });
    }
}

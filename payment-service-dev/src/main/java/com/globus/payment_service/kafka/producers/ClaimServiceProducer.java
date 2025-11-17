package com.globus.payment_service.kafka.producers;

import com.globus.payment_service.dto.TransactionCompletionEvent;
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
public class ClaimServiceProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${PAYMENT_CLAIM_DIRECT_TOPIC}")
    String topic;

    public CompletableFuture<Object> sendResponseForClaimService(TransactionCompletionEvent response) {
        Message<TransactionCompletionEvent> messageCompletionEvent = MessageBuilder
                .withPayload(response)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        log.info("Response prepared with claim ID: {}", response.getClaimId());

        return kafkaTemplate.send(messageCompletionEvent)
                .thenApply(sendResult -> {
                    log.info("Successful sending process in Claim Service with claim ID: {}", response.getClaimId());
                    return null;
                })
                .exceptionally(ex -> {
                    log.error("Failed sending process in Claim Service with claim ID: {}", response.getClaimId());
                    throw new CompletionException("Failed sending in Claim Service", ex);
                });
    }
}

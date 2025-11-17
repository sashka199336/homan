package com.globus.claim_service.service.kafka.producer;

import com.globus.claim_service.event.AccountRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountProducerKafkaService {
    private final KafkaTemplate<String, AccountRequest> kafkaTemplate;
    @Value("${spring.kafka.topic.account-events}")
    private String accountEventTopic;

    public void sendAccountRequest(AccountRequest request) {
        log.info("Sending Account Request to topic {} with CustomerId {}", accountEventTopic, request.getCustomerId());
        CompletableFuture<?> future = kafkaTemplate.send(
                accountEventTopic,
                request.getCustomerId(),
                request
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Account Request sent successfully: {}", request.getClaimId());
            } else {
                log.error("Failed to send Account Request: {}", request.getClaimId(), ex);
            }
        });
    }
}

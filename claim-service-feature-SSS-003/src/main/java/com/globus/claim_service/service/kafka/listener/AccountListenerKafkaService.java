package com.globus.claim_service.service.kafka.listener;

import com.globus.claim_service.event.AccountResponse;
import com.globus.claim_service.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountListenerKafkaService {
    private final ClaimRepository claimRepository;

    @KafkaListener(topics = "claim_direct", groupId = "${spring.kafka.consumer.group-id}")
    public void handleResponse(AccountResponse response) {
        log.info("Received Account Response from topic claim_direct with ClimeId {}", response.getClaimId());
        claimRepository.updateAccountStatus(
                UUID.fromString(response.getClaimId()),
                response.getToBill(),
                response.getStatus(),
                Instant.now());
        log.info("Получен ответ от AS: {}", response);
    }
}

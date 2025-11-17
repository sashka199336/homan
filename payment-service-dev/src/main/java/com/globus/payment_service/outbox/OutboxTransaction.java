package com.globus.payment_service.outbox;

import com.globus.payment_service.kafka.producers.AccountServiceProducer;
import com.globus.payment_service.kafka.producers.ClaimServiceProducer;
import com.globus.payment_service.outbox.entity.OutboxPhs;
import com.globus.payment_service.outbox.entity.OutboxToClaim;
import com.globus.payment_service.outbox.repository.OutboxPhsRepository;
import com.globus.payment_service.outbox.repository.OutboxToClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxTransaction {
    private final OutboxPhsRepository repository;
    private final AccountServiceProducer producer;
    private final OutboxToClaimRepository toClaimRepository;
    private final ClaimServiceProducer toClaimProducer;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOutboxRecord(OutboxPhs message) {
        producer.sendAccountServiceDataToTransfer(message.getPayload());
        repository.deleteById(message.getId());
        log.info("Outbox phs table cleaned, record with id {} deleted, message sent", message.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOutboxToClaimRecord(OutboxToClaim message) {
        toClaimProducer.sendResponseForClaimService(message.getPayload());
        toClaimRepository.deleteById(message.getId());
        log.info("Outbox to Claim service table cleaned, record with id {} deleted, message sent", message.getId());
    }
}

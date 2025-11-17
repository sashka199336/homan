package com.globus.payment_service.outbox;

import com.globus.payment_service.outbox.entity.OutboxPhs;
import com.globus.payment_service.outbox.entity.OutboxToClaim;
import com.globus.payment_service.outbox.repository.OutboxPhsRepository;
import com.globus.payment_service.outbox.repository.OutboxToClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxMasterScheduler {
    private final OutboxPhsRepository repository;
    private final OutboxToClaimRepository toClaimRepository;
    private final OutboxTransaction proxy;

    @Scheduled(fixedDelay = 3000L)
    public void outboxProcessor () {
        List<OutboxPhs> unpublished = repository.findAll();
        if (!unpublished.isEmpty()) {
            log.trace("Found {} unpublished messages in OutboxPhs", unpublished.size());
            for (OutboxPhs message : unpublished) {
                proxy.processOutboxRecord(message);
            }
        }

        List<OutboxToClaim> toClaimUnpublished = toClaimRepository.findAll();
        if (!toClaimUnpublished.isEmpty()) {
            log.trace("Found {} unpublished messages in Outbox to Claim", toClaimUnpublished.size());
            for (OutboxToClaim message : toClaimUnpublished) {
                proxy.processOutboxToClaimRecord(message);
            }
        }
    }
}

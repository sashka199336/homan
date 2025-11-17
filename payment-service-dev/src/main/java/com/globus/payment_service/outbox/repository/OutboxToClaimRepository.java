package com.globus.payment_service.outbox.repository;

import com.globus.payment_service.outbox.entity.OutboxToClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OutboxToClaimRepository extends JpaRepository<OutboxToClaim, UUID> {
}

package com.globus.payment_service.outbox.repository;

import com.globus.payment_service.outbox.entity.OutboxPhs;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OutboxPhsRepository extends JpaRepository<OutboxPhs, UUID> {
}

package com.projectbank.loan.repository;

import com.projectbank.loan.entity.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, UUID> {
    List<OutboxMessage> findAllByProcessedFalse();
}

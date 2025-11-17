package com.globus_bank.customer_service.repository;

import com.globus_bank.customer_service.entity.LegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LegalRepository extends JpaRepository<LegalEntity, UUID> {

    Optional<LegalEntity> findByCustomerId(UUID customerId);
    
    Optional<LegalEntity> findTopByCustomer_IdOrderByUpdatedAtDesc(UUID customerId);
}

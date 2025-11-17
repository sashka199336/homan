package com.globus_bank.customer_service.repository;

import com.globus_bank.customer_service.entity.PassportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassportRepository extends JpaRepository<PassportEntity, UUID> {
    
    Optional<PassportEntity> findByDocumentId(UUID documentId);
}

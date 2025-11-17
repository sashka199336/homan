package com.globus_bank.customer_service.repository;

import com.globus_bank.customer_service.entity.DocumentsTypesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentsTypesRepository extends JpaRepository<DocumentsTypesEntity, UUID> {

    List<DocumentsTypesEntity> findAllByCustomerId(UUID customerId);
}

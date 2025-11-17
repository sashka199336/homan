package com.globus_bank.customer_service.repository;

import com.globus_bank.customer_service.entity.IndividualEntrepreneurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IndividualEntrepreneurRepository extends JpaRepository<IndividualEntrepreneurEntity, UUID> {

    Optional<IndividualEntrepreneurEntity> findByCustomerId(UUID customerId);
    
    Optional<IndividualEntrepreneurEntity> findTopByCustomer_IdOrderByUpdatedAtDesc(UUID customerId);
}

package com.globus_bank.customer_service.repository;

import com.globus_bank.customer_service.entity.ContactsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<ContactsEntity, UUID> {

    List<ContactsEntity> findAllByCustomerId(UUID customerId);
    
    Optional<ContactsEntity> findTopByCustomer_IdOrderByUpdatedAtDesc(UUID customerId);
}

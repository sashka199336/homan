package com.globus_bank.customer_service.repository;

import com.globus_bank.customer_service.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
    
    List<AddressEntity> findAllByCustomerId(UUID customerId);
    
    Optional<AddressEntity> findTopByCustomer_IdOrderByUpdatedAtDesc(UUID customerId);
}

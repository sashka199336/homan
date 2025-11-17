package com.globus_bank.customer_service.repository;

import com.globus_bank.customer_service.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    
    boolean existsByInn(String inn);
    
    Optional<CustomerEntity> findByInn(String inn);
}

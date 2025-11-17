package com.globus_bank.customer_service.repository;

import com.globus_bank.customer_service.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {

    List<BankAccountEntity> findAllByCustomerId(UUID customerId);
}

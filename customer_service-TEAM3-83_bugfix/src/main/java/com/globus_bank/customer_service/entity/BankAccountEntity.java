package com.globus_bank.customer_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bank_accounts")
public class BankAccountEntity extends BaseEntity {
    
    private String settlementAccount;
    
    private String correspondentAccount;
    
    private String bic;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;
}

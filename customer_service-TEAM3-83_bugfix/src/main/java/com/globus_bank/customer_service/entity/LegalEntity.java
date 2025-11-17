package com.globus_bank.customer_service.entity;

import com.globus_bank.customer_service.entity.enums.Position;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "legal_entities")
public class LegalEntity extends BaseEntity {
    
    private String companyName;
    
    private String ogrn;
    
    private String kpp;
    
    @Enumerated(EnumType.STRING)
    private Position position;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;
}

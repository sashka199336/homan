package com.globus_bank.customer_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "individual_entrepreneurs")
public class IndividualEntrepreneurEntity extends BaseEntity {
    
    private String entrepreneurName;
    
    private String ogrnip;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;
}

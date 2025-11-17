package com.globus_bank.customer_service.entity;

import com.globus_bank.customer_service.entity.enums.AddressType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "addresses")
public class AddressEntity extends BaseEntity {
    
    private String country;
    
    private String city;
    
    private String street;
    
    private String houseNumber;
    
    private String apartmentNumber;
    
    private String postalCode;
    
    @Enumerated(EnumType.STRING)
    private AddressType addressType;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;
}

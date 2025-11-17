package com.globus_bank.customer_service.entity;

import com.globus_bank.customer_service.entity.enums.Channel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contacts")
public class ContactsEntity extends BaseEntity {
    
    private String phoneNumber;
    
    private String email;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;
    
    @Enumerated(EnumType.STRING)
    private Channel channel;
}

package com.globus_bank.customer_service.entity;

import com.globus_bank.customer_service.entity.enums.CustomerType;
import com.globus_bank.customer_service.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customers")
public class CustomerEntity extends BaseEntity {
    
    private String inn;
    
    @Enumerated(EnumType.STRING)
    private CustomerType customerType;
    
    @Enumerated(EnumType.STRING)
    private Status statusType;
    
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private IndividualEntrepreneurEntity individualEntrepreneurEntity;
    
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private LegalEntity legalEntity;
    
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactsEntity> contactsEntityList;
    
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankAccountEntity> bankAccountEntityList;
    
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddressEntity> addressEntityList;
    
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentsTypesEntity> documentsTypesEntityList;
}

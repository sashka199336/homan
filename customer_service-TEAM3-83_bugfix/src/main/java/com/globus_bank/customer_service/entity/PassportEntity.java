package com.globus_bank.customer_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer_passport")
public class PassportEntity extends BaseEntity {
    
    private String firstName;
    
    private String lastName;
    
    private String patronymic;
    
    private LocalDate dateOfBirth;
    
    private String series;
    
    private String number;
    
    private LocalDate issueDate;
    
    private String issuedBy;
    
    private String issueCode;
    
    @OneToOne(mappedBy = "passport")
    private DocumentsTypesEntity document;
}

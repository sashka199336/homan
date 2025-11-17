package com.globus_bank.customer_service.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CustomerProfileDto {
    
    private String country;
    
    private String city;
    
    private String street;
    
    private String houseNumber;
    
    private String apartmentNumber;
    
    private String postalCode;
    
    private String addressType;
   
    private String phoneNumber;
    
    private String mail;
    
    private String inn;
    
    private String customerStatus;
  
    private String ogrnip;
    
    private String entrepreneurName;
   
    private String kpp;
    
    private String ogrn;
    
    private String companyName;
    
    private String position;
   
    private String firstName;
    
    private String lastName;
    
    private String patronymic;
    
    private LocalDate dateOfBirth;
    
    private String series;
    
    private String number;
    
    private LocalDate issueDate;
    
    private String issuedBy;
    
    private String issueCode;
}

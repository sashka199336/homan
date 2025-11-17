package com.globus_bank.customer_service.dto.common;

import jakarta.validation.Valid;
import lombok.Data;
import java.util.List;

@Data
public class FullCustomerDto {
    
    @Valid
    private CustomerDto customer;
    
    @Valid
    private List<ContactsDto> contacts;
    
    @Valid
    private List<BankAccountDto> bankAccounts;
    
    @Valid
    private List<AddressDto> addresses;
    
    @Valid
    private List<DocumentsTypeDto> documents;
    
    @Valid
    private PassportDto passport;
    
    @Valid
    private LegalDto legalEntity;
    
    @Valid
    private IndividualEntrepreneurDto individualEntrepreneur;
}

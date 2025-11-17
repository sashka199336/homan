package com.globus_bank.customer_service.dto.common;

import com.globus_bank.customer_service.entity.enums.CustomerType;
import com.globus_bank.customer_service.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CustomerDto {
    
    private UUID id;
    
    private UUID customerId;
    
    @NotBlank(message = "ИНН не должен быть пустым")
    @Pattern(regexp = "\\d{10}|\\d{12}", message = "ИНН должен содержать 10 или 12 цифр")
    private String inn;
    
    @NotNull(message = "Тип клиента не должен быть пустым")
    private CustomerType customerType;
    
    @NotNull(message = "Статус клиента не должен быть пустым")
    private Status statusType;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    private List<ContactsDto> contactsEntityList;
    
    private List<BankAccountDto> bankAccountEntityList;
    
    private List<AddressDto> addressEntityList;
    
    private List<DocumentsTypeDto> documentsTypesEntityList;
    
    private LegalDto legalEntity;
    
    private IndividualEntrepreneurDto individualEntrepreneurEntity;
}

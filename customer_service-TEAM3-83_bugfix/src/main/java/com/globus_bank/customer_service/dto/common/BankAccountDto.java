package com.globus_bank.customer_service.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BankAccountDto {
    
    private UUID id;
    
    private UUID customerId;
    
    @NotBlank(message = "Расчетный счет не должен быть пустым")
    @Pattern(regexp = "\\d{20}", message = "Расчетный счет должен содержать ровно 20 цифр")
    private String settlementAccount;
    
    @NotBlank(message = "Корреспондентский счет не должен быть пустым")
    @Pattern(regexp = "\\d{20}", message = "Корреспондентский счет должен содержать ровно 20 цифр")
    private String correspondentAccount;
    
    @NotBlank(message = "БИК не должен быть пустым")
    @Pattern(regexp = "[A-Za-z0-9]{9,11}", message = "БИК должен содержать от 9 до 11 буквенно-цифровых символов")
    private String bic;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}

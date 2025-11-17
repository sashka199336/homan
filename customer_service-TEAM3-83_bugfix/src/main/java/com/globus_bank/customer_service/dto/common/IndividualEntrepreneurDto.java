package com.globus_bank.customer_service.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class IndividualEntrepreneurDto {
    
    private UUID id;
    
    private UUID customerId;
    
    @NotBlank(message = "ОГРНИП не должен быть пустым")
    @Pattern(regexp = "\\d{15}", message = "ОГРНИП должен содержать 15 цифр")
    private String ogrnip;
    
    @NotBlank(message = "Имя предпринимателя не должно быть пустым")
    private String entrepreneurName;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}

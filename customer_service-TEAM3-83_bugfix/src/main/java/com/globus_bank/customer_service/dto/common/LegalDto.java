package com.globus_bank.customer_service.dto.common;

import com.globus_bank.customer_service.entity.enums.Position;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LegalDto {
    
    private UUID id;
    
    private UUID customerId;
    
    @NotBlank(message = "КПП не должен быть пустым")
    @Pattern(regexp = "\\d{9}", message = "КПП должен содержать 9 цифр")
    private String kpp;
    
    @NotBlank(message = "ОГРН не должен быть пустым")
    @Pattern(regexp = "\\d{13}", message = "ОГРН должен содержать 13 цифр")
    private String ogrn;
    
    @NotBlank(message = "Название компании не должно быть пустым")
    private String companyName;
    
    private Position position;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}

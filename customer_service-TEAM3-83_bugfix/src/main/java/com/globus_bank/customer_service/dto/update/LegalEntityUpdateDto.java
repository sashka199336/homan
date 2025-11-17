package com.globus_bank.customer_service.dto.update;

import com.globus_bank.customer_service.entity.enums.Position;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LegalEntityUpdateDto {
    
    @NotBlank(message = "КПП не должен быть пустым")
    @Pattern(regexp = "\\d{9}", message = "КПП должен содержать 9 цифр")
    private String kpp;
    
    @NotBlank(message = "ОГРН не должен быть пустым")
    @Pattern(regexp = "\\d{13}", message = "ОГРН должен содержать 13 цифр")
    private String ogrn;
    
    @NotBlank(message = "Название компании не должно быть пустым")
    private String companyName;
    
    private Position position;
}

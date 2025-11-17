package com.globus_bank.customer_service.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class IndividualEntrepreneurUpdateDto {
    
    @NotBlank(message = "ОГРНИП не должен быть пустым")
    @Pattern(regexp = "\\d{15}", message = "ОГРНИП должен содержать 15 цифр")
    private String ogrnip;
    
    @NotBlank(message = "Имя предпринимателя не должно быть пустым")
    private String entrepreneurName;
}

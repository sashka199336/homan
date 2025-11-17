package com.globus_bank.customer_service.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BankAccountUpdateDto {
    
    @NotBlank(message = "Расчетный счет не должен быть пустым")
    @Pattern(regexp = "\\d{20}", message = "Расчетный счет должен содержать ровно 20 цифр")
    private String settlementAccount;
    
    @NotBlank(message = "Корреспондентский счет не должен быть пустым")
    @Pattern(regexp = "\\d{20}", message = "Корреспондентский счет должен содержать ровно 20 цифр")
    private String correspondentAccount;
    
    @NotBlank(message = "БИК не должен быть пустым")
    @Pattern(regexp = "[A-Za-z0-9]{9,11}", message = "БИК должен содержать от 9 до 11 буквенно-цифровых символов")
    private String bic;
}

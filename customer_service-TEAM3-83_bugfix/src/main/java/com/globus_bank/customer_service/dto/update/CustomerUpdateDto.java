package com.globus_bank.customer_service.dto.update;

import com.globus_bank.customer_service.entity.enums.CustomerType;
import com.globus_bank.customer_service.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerUpdateDto {
    
    @NotBlank(message = "ИНН не должен быть пустым")
    @Pattern(regexp = "\\d{10}|\\d{12}", message = "ИНН должен содержать 10 или 12 цифр")
    private String inn;
    
    @NotNull(message = "Тип клиента не должен быть пустым")
    private CustomerType customerType;
    
    @NotNull(message = "Статус клиента не должен быть пустым")
    private Status statusType;
}

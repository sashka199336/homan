package com.globus_bank.customer_service.dto.update;

import com.globus_bank.customer_service.entity.enums.Channel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ContactsUpdateDto {
    
    @NotBlank(message = "Номер телефона не должен быть пустым")
    @Pattern(regexp = "^\\+\\d{10,15}$", message = "Номер телефона должен быть в международном формате, например, +1234567890")
    private String phoneNumber;
    
    @NotBlank(message = "Электронная почта не должна быть пустой")
    @Email(message = "Электронная почта должна быть валидной")
    private String email;
    
    @NotNull(message = "Канал должен быть указан")
    private Channel channel;
}

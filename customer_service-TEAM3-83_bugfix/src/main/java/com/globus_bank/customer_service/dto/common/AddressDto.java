package com.globus_bank.customer_service.dto.common;

import com.globus_bank.customer_service.entity.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AddressDto {
    
    private UUID id;
    
    private UUID customerId;
    
    @NotBlank(message = "Страна обязательна")
    private String country;
    
    @NotBlank(message = "Город обязателен")
    private String city;
    
    @NotBlank(message = "Улица обязательна")
    private String street;
    
    @NotBlank(message = "Номер дома обязателен")
    private String houseNumber;
    
    private String apartmentNumber;
    
    @NotBlank(message = "Почтовый код обязательный")
    @Pattern(regexp = "\\d{5,6}", message = "Почтовый код содержит 5 или 6 цифр")
    private String postalCode;
    
    @NotNull(message = "Тип адреса обязателен")
    private AddressType addressType;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}

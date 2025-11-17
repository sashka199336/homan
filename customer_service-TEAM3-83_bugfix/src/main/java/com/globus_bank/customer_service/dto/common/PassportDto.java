package com.globus_bank.customer_service.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PassportDto {
    
    private UUID id;
    
    private UUID documentId;
    
    @NotBlank(message = "Имя не должно быть пустым")
    private String firstName;
    
    @NotBlank(message = "Фамилия не должна быть пустой")
    private String lastName;
    
    private String patronymic;
    
    @NotNull(message = "Дата рождения не должна быть пустой")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "Серия не должна быть пустой")
    @Pattern(regexp = "\\d{4}", message = "Серия должна содержать 4 цифры")
    private String series;
    
    @NotBlank(message = "Номер не должен быть пустым")
    @Pattern(regexp = "\\d{6}", message = "Номер должен содержать 6 цифр")
    private String number;
    
    @NotNull(message = "Дата выдачи не должна быть пустой")
    private LocalDate issueDate;
    
    @NotBlank(message = "Кем выдано не должно быть пустым")
    private String issuedBy;
    
    @NotBlank(message = "Код подразделения не должен быть пустым")
    @Pattern(regexp = "\\d{3}-\\d{3}", message = "Код подразделения должен соответствовать шаблону XXX-XXX")
    private String issueCode;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}

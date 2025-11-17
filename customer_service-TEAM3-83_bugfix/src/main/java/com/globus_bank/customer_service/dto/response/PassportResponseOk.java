package com.globus_bank.customer_service.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PassportResponseOk {
    
    private UUID id;

    private String firstName;

    private String lastName;

    private String patronymic;

    private LocalDate dateOfBirth;

    private String series;

    private String number;

    private LocalDate issueDate;

    private String issuedBy;

    private String issueCode;
}

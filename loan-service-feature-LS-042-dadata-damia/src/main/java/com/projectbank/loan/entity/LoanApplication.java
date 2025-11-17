package com.projectbank.loan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue
    private UUID id;


    @Column(name = "external_application_id", nullable = false, unique = true)
    private String externalApplicationId;

    private String fullName;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "inn_or_ogrn")
    private String innOrOgrn;

    private LocalDate birthDate;

    @Column(name = "company_name")
    private String companyName;

    private String status;

    private boolean dadataPassportValid;
    private boolean dadataCompanyValid;
    private boolean damiaRiskValid;
    private boolean damiaFinanceValid;

    private LocalDate createdAt;
}

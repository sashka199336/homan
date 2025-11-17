package com.globus.claim_service.dto.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDate;

@Data
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfileDto {
    private String country;
    private String city;
    private String street;
    @JsonProperty("house_number")
    private String houseNumber;
    @JsonProperty("apartment_number")
    private String apartmentNumber;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("address_type")
    private String addressType;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String mail;
    private String inn;
    @JsonProperty("customer_status")
    private String customerStatus;
    private String ogrnip;
    @JsonProperty("entrepreneur_name")
    private String entrepreneurName;
    private String kpp;
    private String ogrn;
    @JsonProperty("company_name")
    private String companyName;
    private String position;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String patronymic;
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    private String series;
    private String number;
    @JsonProperty("issue_date")
    private LocalDate issueDate;
    @JsonProperty("issue_by")
    private String issuedBy;
    @JsonProperty("issue_code")
    private String issueCode;
}


package com.projectbank.loan.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ClaimDocumentPackageDto(
        UUID claimId,
        Double amount,
        Integer creditTermMonths,
        String companyName,
        String entrepreneurName,
        String inn,
        String ogrn,
        String kpp,
        String ogrnip,
        String position,
        String firstName,
        String lastName,
        String patronymic,
        LocalDate dateOfBirth,
        String passportSeries,
        String passportNumber,
        LocalDate passportIssueDate,
        String passportIssuedBy,
        String passportIssueCode,
        List<Address> addresses,
        List<Contact> contacts
) {
    public record Address(
            String country,
            String city,
            String street,
            String houseNumber,
            String apartmentNumber,
            String postalCode,
            String addressType
    ) {}

    public record Contact(
            String phoneNumber,
            String email
    ) {}
}

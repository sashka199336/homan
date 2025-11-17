package com.globus_bank.customer_service.customer_service;

import com.globus_bank.customer_service.dto.common.*;
import com.globus_bank.customer_service.entity.*;
import com.globus_bank.customer_service.entity.enums.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.*;

@Component
public class TestDataUtils {

    public static AddressEntity createAddress() {
        AddressEntity address = new AddressEntity();
        address.setPostalCode("222222");
        address.setAddressType(AddressType.REGISTRATION);
        address.setApartmentNumber("22");
        address.setStreet("street2");
        address.setCountry("Country1");
        address.setHouseNumber("2");
        address.setCity("city2");

        return address;
    }

    public static DocumentsTypesEntity createDocumentType() {
        DocumentsTypesEntity documentType = new DocumentsTypesEntity();
        documentType.setAdditionalInformation(new HashMap<>());
        documentType.setDocumentScan(new byte[]{1, 2, 3});
        documentType.setDocumentDate(LocalDate.now());

        return documentType;
    }

    public static BankAccountEntity createBankAccount() {
        BankAccountEntity bankAccount = new BankAccountEntity();
        bankAccount.setSettlementAccount("12345678900987654321");
        bankAccount.setCorrespondentAccount("09876543211234567890");
        bankAccount.setBic("044525225");

        return bankAccount;
    }

    public static IndividualEntrepreneurEntity createIndividualEntrepreneur() {
        IndividualEntrepreneurEntity ie = new IndividualEntrepreneurEntity();
        ie.setOgrnip("123456789012345");
        ie.setEntrepreneurName("Name");

        return ie;
    }

    public static PassportEntity createPassport() {
        PassportEntity passport = new PassportEntity();
        passport.setSeries("1234");
        passport.setNumber("567890");
        passport.setIssuedBy("issuedBy");
        passport.setIssueDate(LocalDate.of(2020, 1, 15));
        passport.setDateOfBirth(LocalDate.of(1990, 5, 20));
        passport.setLastName("LastName");
        passport.setIssueCode("444-444");
        passport.setFirstName("FirstName");
        passport.setPatronymic("Patronymic");

        return passport;
    }

    public static ContactsEntity createContact() {
        ContactsEntity contact = new ContactsEntity();
        contact.setChannel(Channel.EMAIL);
        contact.setEmail("user1@example.com");
        contact.setPhoneNumber("+2234567890");

        return contact;
    }

    public static LegalEntity createLegalEntity() {
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setPosition(Position.CEO);
        legalEntity.setKpp("111111111");
        legalEntity.setOgrn("1234567890123");
        legalEntity.setCompanyName("LegalName");

        return legalEntity;
    }

    public static CustomerEntity createCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerType(CustomerType.LEGAL_ENTITY);
        customer.setInn("1111111111");
        customer.setStatusType(Status.ACTIVE);

        return customer;
    }

    public static ContactsDto createContactDto() {
        ContactsDto contact = new ContactsDto();
        contact.setChannel(Channel.EMAIL);
        contact.setEmail("user1@example.com");
        contact.setPhoneNumber("+2234567890");

        return contact;
    }

    public static AddressDto createAddressDto() {
        AddressDto address = new AddressDto();
        address.setPostalCode("222222");
        address.setAddressType(AddressType.REGISTRATION);
        address.setApartmentNumber("22");
        address.setStreet("street2");
        address.setCountry("Country1");
        address.setHouseNumber("2");
        address.setCity("city2");

        return address;
    }

    public static DocumentsTypeDto createDocumentTypeDto() {
        DocumentsTypeDto documentType = new DocumentsTypeDto();
        documentType.setAdditionalInformation(new HashMap<>());
        documentType.setDocumentScan(new byte[]{1, 2, 3});
        documentType.setDocumentDate(LocalDate.now());

        return documentType;
    }

    public static BankAccountDto createBankAccountDto() {
        BankAccountDto bankAccount = new BankAccountDto();
        bankAccount.setSettlementAccount("12345678900987654321");
        bankAccount.setCorrespondentAccount("09876543211234567890");
        bankAccount.setBic("044525225");

        return bankAccount;
    }

    public static IndividualEntrepreneurDto createIndividualEntrepreneurDto() {
        IndividualEntrepreneurDto ie = new IndividualEntrepreneurDto();
        ie.setOgrnip("123456789012345");
        ie.setEntrepreneurName("Name");

        return ie;
    }

    public static PassportDto createPassportDto() {
        PassportDto passport = new PassportDto();
        passport.setSeries("1234");
        passport.setNumber("567890");
        passport.setIssuedBy("issuedBy");
        passport.setIssueDate(LocalDate.of(2020, 1, 15));
        passport.setDateOfBirth(LocalDate.of(1990, 5, 20));
        passport.setLastName("LastName");
        passport.setIssueCode("444-444");
        passport.setFirstName("FirstName");
        passport.setPatronymic("Patronymic");

        return passport;
    }

    public static LegalDto createLegalDto() {
        LegalDto legalEntity = new LegalDto();
        legalEntity.setPosition(Position.CEO);
        legalEntity.setKpp("111111111");
        legalEntity.setOgrn("1234567890123");
        legalEntity.setCompanyName("LegalName");

        return legalEntity;
    }

    public static CustomerDto createCustomerDto() {
        CustomerDto customer = new CustomerDto();
        customer.setCustomerType(CustomerType.LEGAL_ENTITY);
        customer.setInn("1111111111");
        customer.setStatusType(Status.ACTIVE);

        return customer;
    }

    public static FullCustomerDto createFullCustomerDto() {
        List<AddressDto> addressDtos = new ArrayList<>();
        addressDtos.add(createAddressDto());
        addressDtos.add(createAddressDto());

        List<BankAccountDto> bankAccountDtos = new ArrayList<>();
        bankAccountDtos.add(createBankAccountDto());
        bankAccountDtos.add(createBankAccountDto());

        List<DocumentsTypeDto> documentsTypeDtos = new ArrayList<>();
        documentsTypeDtos.add(createDocumentTypeDto());
        documentsTypeDtos.add(createDocumentTypeDto());

        List<ContactsDto> contactsDtos = new ArrayList<>();
        contactsDtos.add(createContactDto());
        contactsDtos.add(createContactDto());

        CustomerDto customerDto = createCustomerDto();
        PassportDto passportDto = createPassportDto();

        FullCustomerDto fullCustomerDto = new FullCustomerDto();
        fullCustomerDto.setCustomer(customerDto);
        fullCustomerDto.setContacts(contactsDtos);
        fullCustomerDto.setBankAccounts(bankAccountDtos);
        fullCustomerDto.setAddresses(addressDtos);
        fullCustomerDto.setDocuments(documentsTypeDtos);
        fullCustomerDto.setPassport(passportDto);

        return fullCustomerDto;
    }
}

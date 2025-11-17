package com.globus_bank.customer_service.utils.mapper.dto;

import com.globus_bank.customer_service.dto.common.*;
import com.globus_bank.customer_service.dto.response.CustomerProfileDto;
import com.globus_bank.customer_service.dto.response.CustomerResponseOk;
import com.globus_bank.customer_service.dto.response.PassportResponseOk;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CustomerProfileMapper {
    
    @Mappings({
            @Mapping(source = "addressDto.country", target = "country"),
            @Mapping(source = "addressDto.city", target = "city"),
            @Mapping(source = "addressDto.street", target = "street"),
            @Mapping(source = "addressDto.houseNumber", target = "houseNumber"),
            @Mapping(source = "addressDto.apartmentNumber", target = "apartmentNumber"),
            @Mapping(source = "addressDto.postalCode", target = "postalCode"),
            @Mapping(target = "addressType", expression = "java(addressDto.getAddressType().toString())"),
            @Mapping(source = "contactsDto.phoneNumber", target = "phoneNumber"),
            @Mapping(source = "contactsDto.email", target = "mail"),
            @Mapping(source = "customerResponseOk.inn", target = "inn"),
            @Mapping(source = "customerResponseOk.status", target = "customerStatus"),
            @Mapping(source = "individualEntrepreneurDto.ogrnip", target = "ogrnip"),
            @Mapping(source = "individualEntrepreneurDto.entrepreneurName", target = "entrepreneurName"),
            @Mapping(source = "legalDto.kpp", target = "kpp"),
            @Mapping(source = "legalDto.ogrn", target = "ogrn"),
            @Mapping(source = "legalDto.companyName", target = "companyName"),
            @Mapping(source = "legalDto.position", target = "position"),
            @Mapping(source = "passportResponseOk.firstName", target = "firstName"),
            @Mapping(source = "passportResponseOk.lastName", target = "lastName"),
            @Mapping(source = "passportResponseOk.patronymic", target = "patronymic"),
            @Mapping(source = "passportResponseOk.dateOfBirth", target = "dateOfBirth"),
            @Mapping(source = "passportResponseOk.series", target = "series"),
            @Mapping(source = "passportResponseOk.number", target = "number"),
            @Mapping(source = "passportResponseOk.issueDate", target = "issueDate"),
            @Mapping(source = "passportResponseOk.issuedBy", target = "issuedBy"),
            @Mapping(source = "passportResponseOk.issueCode", target = "issueCode"),
    })
    CustomerProfileDto toCustomerProfileDto(AddressDto addressDto, ContactsDto contactsDto,
                                            CustomerResponseOk customerResponseOk,
                                            IndividualEntrepreneurDto individualEntrepreneurDto,
                                            LegalDto legalDto, PassportResponseOk passportResponseOk);
}

package com.globus_bank.customer_service.utils.mapper.dto;

import com.globus_bank.customer_service.dto.common.CustomerDto;
import com.globus_bank.customer_service.dto.update.CustomerUpdateDto;
import com.globus_bank.customer_service.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {IndividualEntrepreneurMapper.class, LegalEntityMapper.class, AddressMapper.class, BankAccountMapper.class, DocumentTypeMapper.class})
public interface CustomerMapper {
    
    @Mapping(source = "inn", target = "inn")
    @Mapping(source = "customerType", target = "customerType")
    @Mapping(source = "statusType", target = "statusType")
    @Mapping(source = "individualEntrepreneurEntity", target = "individualEntrepreneurEntity")
    @Mapping(source = "legalEntity", target = "legalEntity")
    @Mapping(source = "contactsEntityList", target = "contactsEntityList")
    @Mapping(source = "bankAccountEntityList", target = "bankAccountEntityList")
    @Mapping(source = "addressEntityList", target = "addressEntityList")
    @Mapping(source = "documentsTypesEntityList", target = "documentsTypesEntityList")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    CustomerDto toDto(CustomerEntity entity);
    
    CustomerEntity toEntity(CustomerDto dto);
    
    void updateFromRequest(CustomerUpdateDto request, @MappingTarget CustomerEntity entity);
}

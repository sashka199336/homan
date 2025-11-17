package com.globus_bank.customer_service.utils.mapper.dto;

import com.globus_bank.customer_service.dto.common.BankAccountDto;
import com.globus_bank.customer_service.dto.update.BankAccountUpdateDto;
import com.globus_bank.customer_service.entity.BankAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    
    @Mapping(source = "settlementAccount", target = "settlementAccount")
    @Mapping(source = "correspondentAccount", target = "correspondentAccount")
    @Mapping(source = "bic", target = "bic")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    BankAccountDto toDto(BankAccountEntity entity);
    
    BankAccountEntity toEntity(BankAccountDto dto);
    
    void updateFromRequest(BankAccountUpdateDto request, @MappingTarget BankAccountEntity entity);
}

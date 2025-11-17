package com.globus_bank.customer_service.utils.mapper.dto;

import com.globus_bank.customer_service.dto.common.LegalDto;
import com.globus_bank.customer_service.dto.update.LegalEntityUpdateDto;
import com.globus_bank.customer_service.entity.LegalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LegalEntityMapper {
    
    @Mapping(source = "companyName", target = "companyName")
    @Mapping(source = "ogrn", target = "ogrn")
    @Mapping(source = "kpp", target = "kpp")
    @Mapping(source = "position", target = "position")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    LegalDto toDto(LegalEntity entity);
    
    LegalEntity toEntity(LegalDto dto);
    
    void updateFromRequest(LegalEntityUpdateDto request, @MappingTarget LegalEntity entity);
}

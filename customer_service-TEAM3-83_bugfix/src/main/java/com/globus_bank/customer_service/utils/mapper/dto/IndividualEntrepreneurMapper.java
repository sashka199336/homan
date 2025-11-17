package com.globus_bank.customer_service.utils.mapper.dto;

import com.globus_bank.customer_service.dto.common.IndividualEntrepreneurDto;
import com.globus_bank.customer_service.dto.update.IndividualEntrepreneurUpdateDto;
import com.globus_bank.customer_service.entity.IndividualEntrepreneurEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IndividualEntrepreneurMapper {
    
    @Mapping(source = "ogrnip", target = "ogrnip")
    @Mapping(source = "entrepreneurName", target = "entrepreneurName")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    IndividualEntrepreneurDto toDto(IndividualEntrepreneurEntity entity);
    
    IndividualEntrepreneurEntity toEntity(IndividualEntrepreneurDto dto);
    
    void updateFromRequest(IndividualEntrepreneurUpdateDto request, @MappingTarget IndividualEntrepreneurEntity entity);
}

package com.globus_bank.customer_service.utils.mapper.dto;

import com.globus_bank.customer_service.dto.common.DocumentsTypeDto;
import com.globus_bank.customer_service.dto.update.DocumentsTypeUpdateDto;
import com.globus_bank.customer_service.entity.DocumentsTypesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DocumentTypeMapper {
    
    @Mapping(source = "documentDate", target = "documentDate")
    @Mapping(source = "additionalInformation", target = "additionalInformation")
    @Mapping(source = "documentScan", target = "documentScan")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "passport.id", target = "passportId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    DocumentsTypeDto toDto(DocumentsTypesEntity entity);
    
    DocumentsTypesEntity toEntity(DocumentsTypeDto dto);
    
    void updateFromRequest(DocumentsTypeUpdateDto request, @MappingTarget DocumentsTypesEntity entity);
}

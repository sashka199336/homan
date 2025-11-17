package com.globus_bank.customer_service.utils.mapper.dto;

import com.globus_bank.customer_service.dto.common.PassportDto;
import com.globus_bank.customer_service.dto.update.PassportUpdateDto;
import com.globus_bank.customer_service.entity.PassportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PassportMapper {
    
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "patronymic", target = "patronymic")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "series", target = "series")
    @Mapping(source = "number", target = "number")
    @Mapping(source = "issueDate", target = "issueDate")
    @Mapping(source = "issuedBy", target = "issuedBy")
    @Mapping(source = "issueCode", target = "issueCode")
    @Mapping(source = "document.id", target = "documentId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    PassportDto toDto(PassportEntity entity);
    
    PassportEntity toEntity(PassportDto dto);
    
    void updateFromRequest(PassportUpdateDto request, @MappingTarget PassportEntity entity);
}

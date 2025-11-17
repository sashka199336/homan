package com.globus_bank.customer_service.utils.mapper.dto;

import com.globus_bank.customer_service.dto.common.ContactsDto;
import com.globus_bank.customer_service.dto.update.ContactsUpdateDto;
import com.globus_bank.customer_service.entity.ContactsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "channel", target = "channel")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    ContactsDto toDto(ContactsEntity entity);
    
    ContactsEntity toEntity(ContactsDto dto);
    
    void updateFromRequest(ContactsUpdateDto request, @MappingTarget ContactsEntity entity);
}

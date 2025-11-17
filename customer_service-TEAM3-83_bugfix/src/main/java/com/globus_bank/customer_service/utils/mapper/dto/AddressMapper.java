package com.globus_bank.customer_service.utils.mapper.dto;

import com.globus_bank.customer_service.dto.common.AddressDto;
import com.globus_bank.customer_service.dto.update.AddressUpdateDto;
import com.globus_bank.customer_service.entity.AddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    
    @Mapping(source = "country", target = "country")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "houseNumber", target = "houseNumber")
    @Mapping(source = "apartmentNumber", target = "apartmentNumber")
    @Mapping(source = "postalCode", target = "postalCode")
    @Mapping(source = "addressType", target = "addressType")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    AddressDto toDto(AddressEntity entity);
    
    AddressEntity toEntity(AddressDto dto);
    
    void updateFromRequest(AddressUpdateDto request, @MappingTarget AddressEntity entity);
}

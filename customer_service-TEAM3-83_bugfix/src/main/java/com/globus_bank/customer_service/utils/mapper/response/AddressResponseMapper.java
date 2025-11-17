package com.globus_bank.customer_service.utils.mapper.response;

import com.globus_bank.customer_service.dto.response.AddressResponseOk;
import com.globus_bank.customer_service.entity.AddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AddressResponseMapper {

    @Mapping(source = "addressType", target = "addressType", qualifiedByName = "addressTypeToString")
    AddressResponseOk toResponse(AddressEntity dto);

    @Named("addressTypeToString")
    default String addressTypeToString(Enum<?> addressType) {
        return addressType == null ? null : addressType.toString();
    }
}

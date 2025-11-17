package com.globus_bank.customer_service.utils.mapper.response;

import com.globus_bank.customer_service.dto.response.CustomerResponseOk;
import com.globus_bank.customer_service.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CustomerResponseMapper {
    
    @Mapping(source = "id", target = "customerId")
    @Mapping(source = "customerType", target = "customerType", qualifiedByName = "enumToString")
    @Mapping(source = "statusType", target = "status", qualifiedByName = "enumToString")
    CustomerResponseOk toResponse(CustomerEntity dto);
    
    @Named("enumToString")
    default String enumToString(Enum<?> e) {
        return e == null ? null : e.toString();
    }
    
    @Named("zonedDateTimeToUtcString")
    default String zonedDateTimeToUtcString(java.time.ZonedDateTime zdt) {
        if (zdt == null) return null;
        return zdt.withZoneSameInstant(java.time.ZoneOffset.UTC).toInstant().toString();
    }
}

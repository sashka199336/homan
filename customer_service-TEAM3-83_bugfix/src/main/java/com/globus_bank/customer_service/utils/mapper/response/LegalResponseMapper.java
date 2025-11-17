package com.globus_bank.customer_service.utils.mapper.response;

import com.globus_bank.customer_service.dto.response.LegalResponseOk;
import com.globus_bank.customer_service.entity.LegalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LegalResponseMapper {

    @Mapping(source = "id", target = "legalEntityId")
    @Mapping(source = "position", target = "position", qualifiedByName = "enumToString")
    LegalResponseOk toResponse(LegalEntity dto);

    @Named("enumToString")
    default String enumToString(Enum<?> e) {
        return e == null ? null : e.toString();
    }
}

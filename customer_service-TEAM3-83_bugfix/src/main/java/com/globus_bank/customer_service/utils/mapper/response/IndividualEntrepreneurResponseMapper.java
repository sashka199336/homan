package com.globus_bank.customer_service.utils.mapper.response;

import com.globus_bank.customer_service.dto.response.IndividualEntrepreneurResponseOk;
import com.globus_bank.customer_service.entity.IndividualEntrepreneurEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndividualEntrepreneurResponseMapper {

    @Mapping(source = "id", target = "individualEntrepreneurId")
    @Mapping(source = "entrepreneurName", target = "companyName")
    IndividualEntrepreneurResponseOk toResponse(IndividualEntrepreneurEntity dto);
}

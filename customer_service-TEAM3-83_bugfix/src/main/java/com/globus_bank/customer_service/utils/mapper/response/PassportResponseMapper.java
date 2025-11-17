package com.globus_bank.customer_service.utils.mapper.response;

import com.globus_bank.customer_service.dto.response.PassportResponseOk;
import com.globus_bank.customer_service.entity.PassportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PassportResponseMapper {

    @Mapping(source = "issueCode", target = "issueCode")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    PassportResponseOk toResponse(PassportEntity dto);
}

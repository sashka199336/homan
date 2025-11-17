package com.globus_bank.customer_service.utils.mapper.response;

import com.globus_bank.customer_service.dto.response.BankAccountResponseOk;
import com.globus_bank.customer_service.entity.BankAccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankAccountResponseMapper {

    BankAccountResponseOk toResponse(BankAccountEntity dto);
}

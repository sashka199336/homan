package com.globus.financesystem.mapper;

import com.globus.financesystem.kafka.dto.OpenAccountDto;
import com.globus.financesystem.kafka.dto.OpenAccountResponseDto;
import com.globus.financesystem.model.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OpenAccountResponseMapper {

    @Mapping(target = "claimId", source = "dto.claimId")
    @Mapping(target = "customerId", source = "account.clientId")
    @Mapping(target = "toBill", source = "account.accountNumber")
    @Mapping(target = "status", constant = "account_created")
    OpenAccountResponseDto toDto(OpenAccountDto dto, Account account);
}
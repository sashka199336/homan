package com.globus.financesystem.mapper;

import com.globus.financesystem.kafka.dto.OpenAccountDto;
import com.globus.financesystem.model.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {BigDecimal.class, LocalDateTime.class})
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "clientId", source = "customerId")
    @Mapping(target = "balance", expression = "java(BigDecimal.ZERO)")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    Account toEntity(OpenAccountDto dto);

}

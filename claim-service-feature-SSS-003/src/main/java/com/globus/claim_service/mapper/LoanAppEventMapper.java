package com.globus.claim_service.mapper;

import com.globus.claim_service.dto.customer.CustomerProfileDto;
import com.globus.claim_service.dto.credit.CreditClaimRequest;
import com.globus.claim_service.event.LoanAppEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, Instant.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanAppEventMapper {

    @Mapping(target = "timestamp", expression = "java(Instant.now())")
    @Mapping(source = "claimRq.amount", target = "amount")
    @Mapping(source = "claimRq.creditTermMonth", target = "creditTermMonth")
    @Mapping(source = "customer", target = "customer")
    LoanAppEvent toLoanAppEvent(CreditClaimRequest claimRq, CustomerProfileDto customer);
}

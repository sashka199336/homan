package com.globus.claim_service.mapper;

import com.globus.claim_service.dto.ClaimDto;
import com.globus.claim_service.dto.credit.CreditClaimRequest;
import com.globus.claim_service.event.LoanAppEvent;
import com.globus.claim_service.model.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClaimMapper {

    @Mapping(source = "createdAt", target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "claimId", source = "claimId")
    ClaimDto toDto(Claim claim);

    List<ClaimDto> toDtoList(List<Claim> claims);

    Claim toEntity(CreditClaimRequest creditClaimRequest);

    @Mapping(source = "claimId", target = "claimId")
    @Mapping(source = "customerId", target = "customerId")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "creditTermMonth", target = "creditTermMonth")
    @Mapping(source = "createdAt", target = "timestamp")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "toBill", target = "toBill")
    LoanAppEvent toLoanAppEvent(Claim claim);
}


package com.globus.claim_service.controller;

import com.globus.claim_service.configuration.ClaimApi;
import com.globus.claim_service.dto.ClaimDto;
import com.globus.claim_service.dto.ClaimFilter;
import com.globus.claim_service.dto.ClaimRequestParams;
import com.globus.claim_service.dto.credit.CreditClaimRequest;
import com.globus.claim_service.mapper.ClaimMapper;
import com.globus.claim_service.service.impl.ClaimServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClaimController implements ClaimApi {
    private final ClaimServiceImpl service;
    private final ClaimMapper claimMapper;

    @Override
    public ClaimDto getClaimById(UUID id) {
        return claimMapper.toDto(service.getClaimById(id));
    }

    @Override
    public void createCreditClaim(UUID userId, CreditClaimRequest creditClaimRequest) {
        log.info("Creating credit claim for customer with ID: {} by user with ID: {}",
                creditClaimRequest.getCustomerId(), userId);
        service.createCreditClaim(creditClaimRequest);
    }

    @Override
    public List<ClaimDto> getAllClaims(UUID customerId, @Valid ClaimRequestParams params) {
        ClaimFilter filter = params.toFilter(customerId);
        return claimMapper.toDtoList(
                service.getAllClaimsByFilter(params.getPage(), params.getSize(), filter));
    }

    @Override
    public void acceptCredit(UUID claimId) {
        service.acceptCredit(claimId);
    }

    @Override
    public void deleteClaim(@PathVariable UUID claimId) {
        service.deleteById(claimId);
    }
}

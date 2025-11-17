package com.globus.claim_service.service;

import com.globus.claim_service.dto.ClaimFilter;
import com.globus.claim_service.dto.credit.CreditClaimRequest;
import com.globus.claim_service.model.Claim;
import java.util.List;
import java.util.UUID;

public interface ClaimService {

    Claim getClaimById(UUID id);

    List<Claim> getAllClaimsByFilter(int page, int size, ClaimFilter claimFilter);

    void createCreditClaim(CreditClaimRequest creditClaimRequest);

    void acceptCredit(UUID claimId);

    void deleteById(UUID claimId);

}

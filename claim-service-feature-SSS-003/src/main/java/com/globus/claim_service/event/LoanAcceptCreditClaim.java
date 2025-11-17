package com.globus.claim_service.event;

import com.globus.claim_service.model.enums.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanAcceptCreditClaim {
    private String eventName;
    private UUID claimId;
    private ClaimStatus status;
    private String toBill;
}

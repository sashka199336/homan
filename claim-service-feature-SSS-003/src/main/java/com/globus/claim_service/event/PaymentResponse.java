package com.globus.claim_service.event;

import com.globus.claim_service.model.enums.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID claimId;
    private ClaimStatus claimStatus;
}

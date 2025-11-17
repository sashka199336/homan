package com.globus.claim_service.event;

import com.globus.claim_service.dto.credit.CreditClaimRequest;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class ClaimRejectedEvent {
    private UUID customerId;
    private String reason;
    private CreditClaimRequest originalRequest;
    private Instant timestamp;
}

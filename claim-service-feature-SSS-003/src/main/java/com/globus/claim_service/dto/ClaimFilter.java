package com.globus.claim_service.dto;

import com.globus.claim_service.model.enums.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimFilter {
    private UUID customerId;
    private ClaimStatus claimStatus;
}

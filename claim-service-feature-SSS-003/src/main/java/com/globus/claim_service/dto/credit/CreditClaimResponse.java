package com.globus.claim_service.dto.credit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditClaimResponse {
    @JsonProperty("claim_id")
    private UUID claimId;
    private String status;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}

package com.globus.claim_service.dto.credit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditClaimAcceptDto {
    @JsonProperty("claim_id")
    private String claimId;
    private String status;
}

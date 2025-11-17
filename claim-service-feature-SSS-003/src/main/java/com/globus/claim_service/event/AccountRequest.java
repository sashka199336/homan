package com.globus.claim_service.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    @JsonProperty("claim_id")
    private String claimId;
    @JsonProperty("customer_id")
    private String customerId;
    private String operation;
}

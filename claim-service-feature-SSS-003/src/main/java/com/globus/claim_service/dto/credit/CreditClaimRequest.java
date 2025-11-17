package com.globus.claim_service.dto.credit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditClaimRequest {
    @JsonProperty("customer_id")
    private UUID customerId;
    @JsonProperty("claim_type")
    private String claimType;
    private BigDecimal amount;
    @JsonProperty("credit_term_month")
    private Integer creditTermMonth;
}

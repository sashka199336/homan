package com.globus.claim_service.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.globus.claim_service.dto.customer.CustomerProfileDto;
import com.globus.claim_service.model.enums.ClaimStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanAppEvent {
    @JsonProperty("claim_id")
    private UUID claimId;
    @JsonProperty("customer_id")
    private UUID customerId;
    private BigDecimal amount;
    @JsonProperty("credit_term_months")
    private Integer creditTermMonth;
    private Instant timestamp;
    private ClaimStatus status;
    private CustomerProfileDto customer;
    @JsonProperty("to_bill")
    private String toBill;
}

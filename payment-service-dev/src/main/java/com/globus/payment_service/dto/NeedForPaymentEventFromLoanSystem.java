package com.globus.payment_service.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class NeedForPaymentEventFromLoanSystem {
    private String toBill;
    private BigDecimal amount;
    private UUID claimId;
}

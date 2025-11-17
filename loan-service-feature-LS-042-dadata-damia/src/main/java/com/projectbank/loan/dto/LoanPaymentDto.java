package com.projectbank.loan.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LoanPaymentDto(
        UUID claimId,
        BigDecimal amount,
        String toBill
) {}
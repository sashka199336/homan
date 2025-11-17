package com.projectbank.loan.dto;

import com.projectbank.loan.dto.enums.LoanDecisionStatus;

import java.util.UUID;

public record LoanDecisionDto(
        UUID claimId,
        LoanDecisionStatus status
){}

package com.projectbank.loan.dto;

import java.util.List;
import java.util.UUID;

public record LoanRiskDto(
        UUID requestID,
        String inn,
        List<String> passports
) {}

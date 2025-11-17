package com.projectbank.loan.dto;

import com.projectbank.loan.dto.subclassForRiskLoanDto.CompanyCheck;
import com.projectbank.loan.dto.subclassForRiskLoanDto.CompanyFinancialScoringResponse;
import com.projectbank.loan.dto.subclassForRiskLoanDto.PassportCheck;
import com.projectbank.loan.dto.subclassForRiskLoanDto.RiskModel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RiskLoanDto(
        UUID requestID,
        LocalDateTime creationTime,
        List<PassportCheck> passportsCheckResult,
        CompanyCheck companyCheckResult,
        CompanyFinancialScoringResponse financeScoring,
        RiskModel riskModel
) {}
package com.projectbank.loan.service;

import com.projectbank.loan.dto.RiskLoanDto;
import com.projectbank.loan.entity.LoanApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RiskValidator {

    public void validateLoanApplication(LoanApplication application) {
        if (application == null) {
            throw new IllegalArgumentException("LoanApplication не может быть null");
        }
        if (application.getId() == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }
        if (application.getInnOrOgrn() == null) {
            throw new IllegalArgumentException("ИНН/ОГРН не может быть null");
        }
        if (application.getPassportNumber() == null) {
            throw new IllegalArgumentException("паспорт не может быть null");
        }
    }

    public void validateRiskResponseDto(RiskLoanDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("RiskResponseDto не может быть null");
        }
    }
}

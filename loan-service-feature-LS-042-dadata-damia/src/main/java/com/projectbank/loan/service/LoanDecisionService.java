package com.projectbank.loan.service;

import com.projectbank.loan.dto.LoanDecisionDto;
import com.projectbank.loan.dto.RiskLoanDto;
import com.projectbank.loan.dto.enums.LoanDecisionStatus;
import com.projectbank.loan.dto.subclassForRiskLoanDto.*;
import com.projectbank.loan.dto.subclassForRiskLoanDto.subclass.CompanyDetails;
import com.projectbank.loan.dto.subclassForRiskLoanDto.subclass.CompanyState;
import com.projectbank.loan.kafka.producer.ClaimKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanDecisionService {

    private final ClaimKafkaProducer claimKafkaProducer;

    private static final int MIN_COMPANY_AGE_YEARS = 2;
    private static final double MIN_CURRENT_RATIO = 1.5;
    private static final double MIN_QUICK_RATIO = 0.2;
    private static final double MIN_SALES_PROFITABILITY = 5.0;
    private static final double MIN_NET_PROFIT_MARGIN = 0.0;
    private static final double MIN_FINANCIAL_AUTONOMY = 0.5;

    public void decideLoanApproval(RiskLoanDto riskLoanDto) {
        log.info("Начало проверки заявки: {}", riskLoanDto.requestID());

        boolean passportsOk = checkPassports(riskLoanDto.passportsCheckResult());
        boolean companyOk = checkCompany(riskLoanDto.companyCheckResult());
        boolean financeOk = checkFinancialStability(riskLoanDto.financeScoring());

        if (passportsOk && companyOk && financeOk) {
            log.info("Заявка {} одобрена", riskLoanDto.requestID());
            claimKafkaProducer.sendClaimRequest(
                    new LoanDecisionDto(riskLoanDto.requestID(), LoanDecisionStatus.APPROVED)
            );
        } else {
            log.warn("Заявка {} отклонена. Причины: паспорта={}, компания={}, финансы={}",
                    riskLoanDto.requestID(), passportsOk, companyOk, financeOk);
            claimKafkaProducer.sendClaimRequest(
                    new LoanDecisionDto(riskLoanDto.requestID(), LoanDecisionStatus.REJECTED)
            );
        }
    }

    private boolean checkPassports(List<PassportCheck> passports) {
        if (passports == null || passports.isEmpty()) {
            log.warn("Паспортная проверка: список пуст");
            return false;
        }
        boolean allValid = passports.stream().allMatch(p -> p.getQc() == 0);
        if (!allValid) log.warn("Паспортная проверка: обнаружены некорректные паспорта");
        return allValid;
    }

    private boolean checkCompany(CompanyCheck companyCheck) {
        if (companyCheck == null || companyCheck.getSuggestions() == null ||
                companyCheck.getSuggestions().isEmpty()) {
            log.warn("Проверка компании: нет данных");
            return false;
        }

        CompanyDetails companyData = companyCheck.getSuggestions().get(0).getData();
        if (companyData == null || companyData.getState() == null) {
            log.warn("Проверка компании: нет информации о состоянии компании");
            return false;
        }

        CompanyState companyState = companyData.getState();

        if (!"ACTIVE".equals(companyState.getStatus())) {
            log.warn("Проверка компании: статус не ACTIVE ({})", companyState.getStatus());
            return false;
        }

        if (companyState.getRegistrationDate() == null) {
            log.warn("Проверка компании: нет даты регистрации");
            return false;
        }

        LocalDate registrationDate = Instant.ofEpochMilli(companyState.getRegistrationDate())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate twoYearsAgo = LocalDate.now().minusYears(MIN_COMPANY_AGE_YEARS);

        if (registrationDate.isAfter(twoYearsAgo)) {
            log.warn("Проверка компании: компания моложе {} лет (дата регистрации: {})",
                    MIN_COMPANY_AGE_YEARS, registrationDate);
            return false;
        }

        return true;
    }

    private boolean checkFinancialStability(CompanyFinancialScoringResponse scoring) {
        if (scoring == null || scoring.getCompanies() == null) {
            log.warn("Проверка финансов: нет данных о компаниях");
            return false;
        }

        boolean allOk = scoring.getCompanies().values().stream().allMatch(details ->
                checkLastTwoValues(details.getCurrentRatio(), MIN_CURRENT_RATIO, "CurrentRatio") &&
                        checkLastTwoValues(details.getQuickRatio(), MIN_QUICK_RATIO, "QuickRatio") &&
                        checkLastTwoValues(details.getOperatingProfitMargin(), MIN_SALES_PROFITABILITY, "OperatingProfitMargin") &&
                        checkLastTwoValues(details.getNetProfitMargin(), MIN_NET_PROFIT_MARGIN, "NetProfitMargin") &&
                        checkLastTwoValues(details.getReturnOnEquity(), MIN_FINANCIAL_AUTONOMY, "ReturnOnEquity")
        );

        if (!allOk) log.warn("Проверка финансов: не все показатели соответствуют минимальным требованиям");

        return allOk;
    }

    private boolean checkLastTwoValues(Map<String, CompanyFinancialIndicatorValue> ratioMap,
                                       double minValue,
                                       String metricName) {
        if (ratioMap == null || ratioMap.size() < 2) {
            log.warn("{}: недостаточно данных ({} записей)", metricName,
                    ratioMap == null ? 0 : ratioMap.size());
            return false;
        }

        boolean allAboveMin = ratioMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
                .limit(2)
                .map(e -> e.getValue() != null ? e.getValue().getValue() : null)
                .allMatch(v -> {
                    boolean ok = v != null && v >= minValue;
                    if (!ok) {
                        log.warn("{}: значение {} меньше минимума {}", metricName, v, minValue);
                    }
                    return ok;
                });

        return allAboveMin;
    }

}

package com.projectbank.loan.dto.subclassForRiskLoanDto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFinancialScoringResponse {
    private Map<String, CompanyFinancialDetails> companies = new HashMap<>();

    @JsonAnySetter
    public void setCompany(String inn, CompanyFinancialDetails data) {
        companies.put(inn, data);
    }

    @JsonValue
    public Map<String, CompanyFinancialDetails> getCompanies() {
        return companies;
    }
}








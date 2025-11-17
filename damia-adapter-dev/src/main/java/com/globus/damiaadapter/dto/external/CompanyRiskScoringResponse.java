package com.globus.damiaadapter.dto.external;

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
public class CompanyRiskScoringResponse {
    private Map<String, Map<String, CompanyRiskYearData>> companies = new HashMap<>();

    @JsonAnySetter
    public void setCompany(String inn, Map<String, CompanyRiskYearData> data) {
        companies.put(inn, data);
    }

    @JsonValue
    public Map<String, Map<String, CompanyRiskYearData>> getCompanies() {
        return companies;
    }
}


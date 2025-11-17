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
public class CompanyRiskYearData {
    private Map<String, CompanyRiskData> years = new HashMap<>();

    @JsonAnySetter
    public void addYearData(String year, CompanyRiskData data) {
        years.put(year, data);
    }

    @JsonValue
    public Map<String, CompanyRiskData> getYears() {
        return years;
    }
}

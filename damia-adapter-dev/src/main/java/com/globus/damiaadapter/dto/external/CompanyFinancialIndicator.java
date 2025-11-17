package com.globus.damiaadapter.dto.external;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFinancialIndicator {
    private Map<String, CompanyFinancialIndicatorValue> valuesByYear = new HashMap<>();

    @JsonAnySetter
    public void addYearData(String year, CompanyFinancialIndicatorValue value) {
        valuesByYear.put(year, value);
    }
}


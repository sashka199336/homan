package com.projectbank.loan.dto.subclassForRiskLoanDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFinancialIndicatorValue {
    @JsonProperty("Знач")
    private Double value;

    @JsonProperty("Норма")
    private Double norm;

    @JsonProperty("НормаНижн")
    private Double lowerNorm;

    @JsonProperty("НормаВерхн")
    private Double upperNorm;

    @JsonProperty("Балл")
    private Integer score;

    @JsonProperty("НормаСравн")
    private String normComparison;
}
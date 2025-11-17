package com.globus.damiaadapter.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFinancialData {
@JsonProperty("КоэфОборЗапасов")
private Map<String, CompanyFinancialIndicatorValue> inventoryTurnoverRatio;
    @JsonProperty("ПериодОборЗапасов")
    private Map<String, CompanyFinancialIndicatorValue> inventoryTurnoverPeriod;

    @JsonProperty("КоэфОборДЗ")
    private Map<String, CompanyFinancialIndicatorValue> receivablesTurnoverRatio;

    @JsonProperty("ПериодОборДЗ")
    private Map<String, CompanyFinancialIndicatorValue> receivablesTurnoverPeriod;

    @JsonProperty("КоэфОборКЗ")
    private Map<String, CompanyFinancialIndicatorValue> payablesTurnoverRatio;

    @JsonProperty("ПериодОборКЗ")
    private Map<String, CompanyFinancialIndicatorValue> payablesTurnoverPeriod;

    @JsonProperty("КоэфОборАктивов")
    private Map<String, CompanyFinancialIndicatorValue> assetTurnoverRatio;

    @JsonProperty("РентАктивов")
    private Map<String, CompanyFinancialIndicatorValue> returnOnAssets;

    @JsonProperty("РентСК")
    private Map<String, CompanyFinancialIndicatorValue> returnOnEquity;

    @JsonProperty("РентПродаж")
    private Map<String, CompanyFinancialIndicatorValue> operatingProfitMargin;

    @JsonProperty("ЧистРентПродаж")
    private Map<String, CompanyFinancialIndicatorValue> netProfitMargin;

    @JsonProperty("КоэфТекЛикв")
    private Map<String, CompanyFinancialIndicatorValue> currentRatio;

    @JsonProperty("КоэфАбсЛикв")
    private Map<String, CompanyFinancialIndicatorValue> quickRatio;

    @JsonProperty("КоэфФинАвт")
    private Map<String, CompanyFinancialIndicatorValue> financialAutonomy;

    @JsonProperty("КоэфФинЗав")
    private Map<String, CompanyFinancialIndicatorValue> financialDependence;

    @JsonProperty("КоэфФинЛевер")
    private Map<String, CompanyFinancialIndicatorValue> financialLeverage;
}


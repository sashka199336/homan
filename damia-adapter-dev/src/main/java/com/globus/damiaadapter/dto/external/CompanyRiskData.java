package com.globus.damiaadapter.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRiskData {
    @JsonProperty("РискЗнач")
    private Double riskValue;

    @JsonProperty("РискЗона")
    private String riskZone;

    @JsonProperty("БаллЗнач")
    private Double scoreValue;

    @JsonProperty("БаллЗона")
    private String scoreZone;

    @JsonProperty("НадежностьЗнач")
    private Double reliabilityValue;

    @JsonProperty("НадежностьЗона")
    private String reliabilityZone;

    @JsonProperty("Показатели")
    private List<RiskIndicator> indicators;
}

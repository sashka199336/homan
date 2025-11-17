package globus.riskaggregatev2.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class RiskIndicator {
    @JsonProperty("Наименование")
    private String name;

    @JsonProperty("Значение")
    private Double value;

    @JsonProperty("nWoE")
    private Double nWoE;
}

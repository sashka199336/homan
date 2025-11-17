package globus.riskaggregatev2.dto.external.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Suggestion {
    private String value;
    @JsonProperty("unrestricted_value")
    private String unrestrictedValue;
    private CompanyData data;
}

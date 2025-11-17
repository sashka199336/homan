package globus.riskaggregatev2.dto.external.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Finance {
    @JsonProperty("tax_system")
    private String taxSystem;
    private double income;
    private double expense;
    private double revenue;
    private Double debt;
    private Double penalty;
    private int year;
}

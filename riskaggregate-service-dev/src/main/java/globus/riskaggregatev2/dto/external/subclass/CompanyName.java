package globus.riskaggregatev2.dto.external.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompanyName {
    @JsonProperty("full_with_opf")
    private String fullWithOpf;
    @JsonProperty("short_with_opf")
    private String shortWithOpf;
    private String latin;
    private String fullName;
    private String shortName;
}

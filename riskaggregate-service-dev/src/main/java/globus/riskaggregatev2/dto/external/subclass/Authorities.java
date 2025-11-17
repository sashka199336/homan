package globus.riskaggregatev2.dto.external.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Authorities {
    @JsonProperty("fts_registration")
    private Authority ftsRegistration;
    @JsonProperty("fts_report")
    private Authority ftsReport;
    private Authority pf;
    private Authority sif;
}

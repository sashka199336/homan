package globus.riskaggregatev2.dto.external.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Management {
    private String name;
    private String post;
    @JsonProperty("start_date")
    private Long startDate;
    private Object disqualified;
}

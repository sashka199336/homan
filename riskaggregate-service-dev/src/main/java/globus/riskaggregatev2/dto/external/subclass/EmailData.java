package globus.riskaggregatev2.dto.external.subclass;

import lombok.Data;

@Data
public class EmailData {
    private String local;
    private String domain;
    private String type;
    private String source;
    private Integer qc;
}

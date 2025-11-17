package globus.riskaggregatev2.dto.external.subclass;

import lombok.Data;

@Data
public class PhoneData {
    private String contact;
    private String source;
    private Integer qc;
    private String type;
    private String number;
}

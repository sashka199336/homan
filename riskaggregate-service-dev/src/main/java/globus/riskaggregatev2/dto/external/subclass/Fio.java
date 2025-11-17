package globus.riskaggregatev2.dto.external.subclass;

import lombok.Data;

@Data
public class Fio {
    private String surname;
    private String name;
    private String patronymic;
    private String gender;
    private String source;
    private Integer qc;
}

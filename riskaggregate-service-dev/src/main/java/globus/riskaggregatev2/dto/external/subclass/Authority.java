package globus.riskaggregatev2.dto.external.subclass;

import lombok.Data;

@Data
// Данные органа, в котором зарегистрирована компания (ФНС регистрации, отчетности, отделение ПФР и т.п.)
public class Authority {
    private String type;
    private String code;
    private String name;
    private String address;
}

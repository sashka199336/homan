package globus.riskaggregatev2.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompanyCheckRequest {
    private String query;     // запрос. Может содержать ИНН или ОГРН
    private int count;        // количество результатов. По умолчанию - 10, максимум 300
    @JsonProperty("branch_type")
    private String branchType; // MAIN для поиска головной организации, BRANCH для филиалов
    private String kpp; // КПП филиала
    private String type; // LEGAL или INDIVIDUAL
}

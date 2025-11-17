package globus.riskaggregatev2.dto.external;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFinancialScoringResponse {
    private Map<String, CompanyFinancialData> companies = new HashMap<>();

    @JsonAnySetter
    public void setCompany(String inn, CompanyFinancialData data) {
        companies.put(inn, data);
    }

    @JsonValue
    public Map<String, CompanyFinancialData> getCompanies() {
        return companies;
    }
}








package globus.riskaggregatev2.dto.external;

import globus.riskaggregatev2.dto.external.subclass.Suggestion;
import lombok.Data;
import java.util.List;

@Data
public class CompanyCheck {
    private List<Suggestion> suggestions;
}


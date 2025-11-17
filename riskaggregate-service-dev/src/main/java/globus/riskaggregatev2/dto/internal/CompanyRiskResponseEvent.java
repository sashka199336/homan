package globus.riskaggregatev2.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRiskResponseEvent {
    private UUID requestID;
    private ResponseStatus requestStatus;
    private CompanyRiskScoringResponse riskScoringResponse;
}

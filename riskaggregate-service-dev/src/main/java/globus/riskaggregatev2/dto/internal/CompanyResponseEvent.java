package globus.riskaggregatev2.dto.internal;

import globus.riskaggregatev2.dto.external.CompanyCheck;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponseEvent {
    private UUID requestID;
    private ResponseStatus responseStatus;
    private CompanyCheck companyCheckResult;
}

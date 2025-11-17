package globus.riskaggregatev2.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CompanyRequestEvent {
    private UUID requestID;
    private String inn;
}

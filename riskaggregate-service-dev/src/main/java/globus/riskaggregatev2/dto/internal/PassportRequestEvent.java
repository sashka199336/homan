package globus.riskaggregatev2.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PassportRequestEvent {
    private UUID requestID;
    private List<String> passports;
}

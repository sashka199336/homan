package globus.riskaggregatev2.dto.internal;

import globus.riskaggregatev2.dto.external.PassportCheck;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassportResponseEvent {
    private UUID requestID;
    private ResponseStatus responseStatus;
    private List<PassportCheck> checkedPassports;
}
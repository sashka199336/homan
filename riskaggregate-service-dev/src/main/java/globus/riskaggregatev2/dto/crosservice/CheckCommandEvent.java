package globus.riskaggregatev2.dto.crosservice;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CheckCommandEvent {
    private UUID requestID;
    private String inn;
    private List<String> passports;
}
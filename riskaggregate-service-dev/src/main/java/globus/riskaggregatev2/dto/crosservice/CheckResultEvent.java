package globus.riskaggregatev2.dto.crosservice;

import globus.riskaggregatev2.dto.external.CompanyCheck;
import globus.riskaggregatev2.dto.external.CompanyFinancialScoringResponse;
import globus.riskaggregatev2.dto.external.PassportCheck;

import globus.riskaggregatev2.dto.internal.CompanyRiskScoringResponse;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CheckResultEvent {
    private UUID requestID;
    private LocalDateTime creationTime;
    private List<PassportCheck> passportsCheckResult;
    private CompanyCheck companyCheckResult;
    private CompanyFinancialScoringResponse financeScoring;
    private CompanyRiskScoringResponse riskModel;
}

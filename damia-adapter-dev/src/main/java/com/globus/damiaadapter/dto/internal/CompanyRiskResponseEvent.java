package com.globus.damiaadapter.dto.internal;

import com.globus.damiaadapter.dto.external.CompanyRiskScoringResponse;
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

    public enum ResponseStatus {
        SUCCESS,
        NOT_FOUND,
        SERVICE_UNAVAILABLE,
        VALIDATION_ERROR
    }
}

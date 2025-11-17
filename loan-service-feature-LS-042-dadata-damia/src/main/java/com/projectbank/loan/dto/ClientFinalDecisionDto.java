package com.projectbank.loan.dto;

import java.util.UUID;

public record ClientFinalDecisionDto(
        UUID claimId,
        String status
) {
}

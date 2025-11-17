package com.globus.financesystem.kafka.dto;

public record OpenAccountDto(
        String claimId,
        String customerId,
        String operation
) {
}

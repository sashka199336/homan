package com.globus.financesystem.kafka.dto;

public record OpenAccountErrorDto(
        String claimId,
        String customerId,
        String status
) {
}

package com.globus.financesystem.kafka.dto;

public record OpenAccountResponseDto(
        String claimId,
        String customerId,
        String toBill,
        String status
) {
}

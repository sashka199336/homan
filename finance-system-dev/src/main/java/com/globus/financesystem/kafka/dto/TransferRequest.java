package com.globus.financesystem.kafka.dto;

import java.math.BigDecimal;

public record TransferRequest(
        String transactionId,
        BigDecimal amount,
        String toBill
) {
}

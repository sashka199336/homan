package com.globus.financesystem.kafka.dto;

import com.globus.financesystem.kafka.dto.enums.TransactionStatus;

import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        TransactionStatus status
) {
}

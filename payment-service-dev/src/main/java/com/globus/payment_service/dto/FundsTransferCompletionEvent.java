package com.globus.payment_service.dto;

import com.globus.payment_service.entity.Status;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class FundsTransferCompletionEvent {
    private UUID transactionId;
    private Status status;
}

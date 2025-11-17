package com.globus.payment_service.service.interfaces;

import com.globus.payment_service.dto.NeedForPaymentEventFromLoanSystem;
import com.globus.payment_service.dto.FundsTransferCompletionEvent;

public interface TransactionService {
    void startTransaction (NeedForPaymentEventFromLoanSystem message);
    void finalizeTransaction (FundsTransferCompletionEvent message);
}

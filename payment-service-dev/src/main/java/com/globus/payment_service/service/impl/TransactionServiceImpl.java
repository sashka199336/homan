package com.globus.payment_service.service.impl;

import com.globus.payment_service.dto.FundsTransferCompletionEvent;
import com.globus.payment_service.dto.NeedForPaymentEventFromLoanSystem;
import com.globus.payment_service.service.TransactionHistoryWrapper;
import com.globus.payment_service.service.interfaces.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionHistoryWrapper wrapper;

    @Override
    @Retryable(retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 60000, maxDelay = 500000, multiplier = 2),
            recover = "recoverActionFailure")
    public void startTransaction(NeedForPaymentEventFromLoanSystem message) {
        log.info("Received payment request for claim Id %s"
                .formatted(message.getClaimId()));
        try {
            wrapper.createTransactionHistory(message);
        } catch (Exception e) {
            int step = Objects.requireNonNull(RetrySynchronizationManager.getContext()).getRetryCount();
            if (step < 4) {
                log.atError()
                        .setCause(e)
                        .setMessage("Attempt %d Failed to create database record for claim Id %s, will be retried"
                                .formatted(step + 1, message.getClaimId()))
                        .log();
            }
            throw e;
        }
    }

    @Override
    @Retryable(retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 60000, maxDelay = 500000, multiplier = 2),
            recover = "recoverActionFailure")
    public void finalizeTransaction(FundsTransferCompletionEvent message) {
        log.info("Response with operation results received for transactionHistory Id %s"
                .formatted(message.getTransactionId()));
        try {
            wrapper.finalizeTransactionStatus(message);
        } catch (Exception e) {
            int step = Objects.requireNonNull(RetrySynchronizationManager.getContext()).getRetryCount();
            if (step < 4) {
                log.atError()
                        .setCause(e)
                        .setMessage("Attempt %d failed to finalize database record with transaction Id %s, will be retried"
                                .formatted(step + 1, message.getTransactionId()))
                        .log();
            }
            throw e;
        }
    }

    @Recover
    public void recoverActionFailure (Exception ex, NeedForPaymentEventFromLoanSystem message) {
        log.atError()
                .setCause(ex)
                .setMessage("Failed to create database record for claim Id %s (account no. (bill): %s; amount: %s), attempts exhausted."
                        .formatted(message.getClaimId(), message.getToBill(), message.getAmount()))
                .log();
    }

    @Recover
    public void recoverActionFailure (Exception ex, FundsTransferCompletionEvent message) {
        log.atError()
                .setCause(ex)
                .setMessage("Failed to update transactionHistory record id %s, attempts exhausted. TransactionHistory status is %s. TransactionHistory was not finalized!"
                        .formatted(message.getTransactionId(), message.getStatus()))
                .log();
    }
}

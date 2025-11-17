package com.globus.payment_service.service;

import com.globus.payment_service.dto.DemandForPaymentEventToAccountService;
import com.globus.payment_service.dto.FundsTransferCompletionEvent;
import com.globus.payment_service.dto.NeedForPaymentEventFromLoanSystem;
import com.globus.payment_service.entity.TransactionHistory;
import com.globus.payment_service.mapper.PaymentHistoryMapper;
import com.globus.payment_service.outbox.entity.OutboxPhs;
import com.globus.payment_service.outbox.entity.OutboxToClaim;
import com.globus.payment_service.outbox.repository.OutboxPhsRepository;
import com.globus.payment_service.outbox.repository.OutboxToClaimRepository;
import com.globus.payment_service.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionHistoryWrapper {
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final OutboxPhsRepository outboxPhsRepository;
    private final OutboxToClaimRepository toClaimRepository;
    private final PaymentHistoryMapper mapper;

    @Transactional
    public void createTransactionHistory (NeedForPaymentEventFromLoanSystem message) {
        TransactionHistory transactionHistory = transactionHistoryRepository.save(mapper.fromLoanMessageToEntity(message));
        log.trace("Created transactionHistory record Id %s for claim Id %s"
                .formatted(transactionHistory.getTransactionId(), transactionHistory.getClaimId()));
        DemandForPaymentEventToAccountService body = mapper.fromEntityToOutboxBody(transactionHistory);
        OutboxPhs outMessage = new OutboxPhs();
        outMessage.setPayload(body);
        outboxPhsRepository.save(outMessage);
        log.trace("Created and saved to outbox record Id %s for transaction Id %s"
                .formatted(outMessage.getId(), body.getTransactionId()));
    }

    @Transactional
    public void finalizeTransactionStatus (FundsTransferCompletionEvent message) {
        Optional<TransactionHistory> subject = transactionHistoryRepository.findById(message.getTransactionId());
        if (subject.isPresent()) {
            TransactionHistory updated = subject.get();
            updated.setStatus(message.getStatus());
            updated = transactionHistoryRepository.save(updated);
            OutboxToClaim outboxToClaim = toClaimRepository.save(new OutboxToClaim(mapper.fromEntityToClaimMessage(updated)));
            log.info("TransactionHistory Id %s processing completed at %s, status is %s"
                    .formatted(updated.getTransactionId(), updated.getUpdateDateTime(), updated.getStatus().name()));
            log.trace("Created and saved outbox-to-claim record Id %s for claim Id %s"
                    .formatted(outboxToClaim.getId(), outboxToClaim.getPayload().getClaimId()));
        } else {
            log.error("No database record found for %s transactionHistory id %s. TransactionHistory was not finalized!"
                    .formatted(message.getStatus(), message.getTransactionId()));
        }
    }
}

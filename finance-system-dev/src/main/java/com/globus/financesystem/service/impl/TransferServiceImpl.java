package com.globus.financesystem.service.impl;

import com.globus.financesystem.kafka.dto.TransferRequest;
import com.globus.financesystem.kafka.dto.TransferResponse;
import com.globus.financesystem.kafka.dto.enums.TransactionStatus;
import com.globus.financesystem.model.entity.Account;
import com.globus.financesystem.repository.AccountRepository;
import com.globus.financesystem.service.TransferService;
import com.globus.financesystem.service.util.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final OutboxService outboxService;

    private static final String ACCOUNT_PATTERN = "ACC-\\d{6}";

    @Override
    @Transactional
    public void processTransferRequest(TransferRequest request) {

        UUID txId = null;
        try {
            if (request.transactionId() != null) {
                txId = UUID.fromString(request.transactionId());
            }
        } catch (IllegalArgumentException e) {
            log.error("[CreditService] Некорректный transactionId: {}", request.transactionId());
            return;
        }

        if (txId == null
                || request.amount() == null || request.amount().signum() <= 0
                || request.toBill() == null || request.toBill().isBlank()
                || !request.toBill().matches(ACCOUNT_PATTERN)) {

            log.warn("[CreditService] Некорректное сообщение или формат счёта, transactionId={}, toBill={}",
                    request.transactionId(), request.toBill());

            TransferResponse response = new TransferResponse(txId, TransactionStatus.REJECTED);
            saveOutboxEvent(response);
            return;
        }

        Optional<Account> optional = accountRepository.findByAccountNumber(request.toBill());
        if (optional.isEmpty()) {
            log.warn("[CreditService] Не найден счёт: {}", request.toBill());
            TransferResponse response = new TransferResponse(txId, TransactionStatus.REJECTED);
            saveOutboxEvent(response);
            return;
        }

        Account account = optional.get();
        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);

        TransferResponse response = new TransferResponse(txId, TransactionStatus.DONE);
        saveOutboxEvent(response);

        log.info("[CreditService] Пополнение счёта: accountNumber={}, newBalance={}, transactionId={}",
                account.getAccountNumber(), account.getBalance(), txId);
    }

    private void saveOutboxEvent(TransferResponse response) {
        String eventType = response.status() == TransactionStatus.DONE
                ? "TransferCompleted"
                : "TransferRejected";

        outboxService.saveEvent(
                "Transfer",
                response.transactionId() != null ? response.transactionId().toString() : "unknown",
                eventType,
                response
        );

        log.info("[CreditService] Событие {} сохранено в outbox, transactionId={}", eventType, response.transactionId());
    }
}

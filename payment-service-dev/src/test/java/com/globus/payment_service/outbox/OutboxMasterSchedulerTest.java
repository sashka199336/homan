package com.globus.payment_service.outbox;

import com.globus.payment_service.AbstractTest;
import com.globus.payment_service.dto.DemandForPaymentEventToAccountService;
import com.globus.payment_service.outbox.entity.OutboxPhs;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OutboxMasterSchedulerTest extends AbstractTest {

    @Test
    void outboxProcessorTest() {
        transactionService.startTransaction(fullMessageFromLoan);
        assertEquals(1, transactionHistoryRepository.findAll().size());
        assertEquals(1, outboxRepository.findAll().size());

        List<OutboxPhs> messages = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            OutboxPhs message = new OutboxPhs();
            message.setPayload(DemandForPaymentEventToAccountService.builder()
                    .transactionId(UUID.randomUUID())
                    .toBill("40817810100001234505")
                    .amount(BigDecimal.valueOf(200000 + i))
                    .build());
            messages.add(message);
        }
        outboxRepository.saveAll(messages);
        assertEquals(5, outboxRepository.findAll().size());
        masterScheduler.outboxProcessor();
        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(30, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals(0, outboxRepository.findAll().size()));
    }
}
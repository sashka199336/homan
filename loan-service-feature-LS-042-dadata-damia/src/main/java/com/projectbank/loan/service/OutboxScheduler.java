package com.projectbank.loan.service;

import com.projectbank.loan.entity.OutboxMessage;
import com.projectbank.loan.exception.OutboxProcessingException;
import com.projectbank.loan.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxMessageRepository outboxMessageRepository;
    private final OutboxService outboxService;

    @Scheduled(fixedDelay = 10000)
    public void processOutboxMessages() {
        List<OutboxMessage> messages = outboxMessageRepository.findAllByProcessedFalse();
        for (OutboxMessage message : messages) {
            try {
                outboxService.processMessage(message);
            } catch (OutboxProcessingException e) {
                log.error("Ошибка обработки outbox-сообщения: {}", message.getId(), e);
            }
        }
    }
}

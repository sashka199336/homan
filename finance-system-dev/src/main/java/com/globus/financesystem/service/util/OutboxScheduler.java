package com.globus.financesystem.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus.financesystem.kafka.dto.OpenAccountErrorDto;
import com.globus.financesystem.kafka.dto.OpenAccountResponseDto;
import com.globus.financesystem.kafka.dto.TransferResponse;
import com.globus.financesystem.model.entity.OutboxEvent;
import com.globus.financesystem.model.enums.OutboxStatus;
import com.globus.financesystem.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.account-payment}")
    private String accountPaymentTopic;

    @Value("${spring.kafka.topic.account-claim}")
    private String accountClaimTopic;

    private static final int MAX_RETRIES = 5;

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {
        log.info("[OUTBOX SCHEDULER] Запуск шедулера для проверки новых событий...");
        List<OutboxEvent> events = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);

        if (events.isEmpty()) {
            log.debug("[OUTBOX] Нет новых сообщений для отправки");
            return;
        }

        log.info("[OUTBOX] Найдено {} новых сообщений для обработки", events.size());

        for (OutboxEvent event : events) {
            try {
                sendSingleEvent(event);
            } catch (Exception e) {
                log.error("[OUTBOX] Ошибка обработки события {}: {}", event.getId(), e.getMessage(), e);
                handleFailedEvent(event);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void sendSingleEvent(OutboxEvent event) throws Exception {
        String topic = determineTopic(event);
        Object payload = deserializePayload(event);

        kafkaTemplate.send(topic, event.getAggregateId(), payload)
                .toCompletableFuture()
                .get(10, TimeUnit.SECONDS);

        event.setStatus(OutboxStatus.SENT);
        event.setLastAttemptAt(LocalDateTime.now());
        outboxRepository.save(event);

        log.info("[OUTBOX] Сообщение {} отправлено в топик {} и помечено как SENT, aggregateId={}",
                event.getType(), topic, event.getAggregateId());
    }

    private void handleFailedEvent(OutboxEvent event) {
        int retries = event.getRetries() + 1;
        event.setRetries(retries);
        event.setLastAttemptAt(LocalDateTime.now());

        if (retries >= MAX_RETRIES) {
            event.setStatus(OutboxStatus.FAILED);
            log.error("[OUTBOX] Событие {} окончательно помечено как FAILED после {} попыток",
                    event.getId(), retries);
        } else {
            event.setStatus(OutboxStatus.NEW);
            log.warn("[OUTBOX] Ошибка отправки события {}, попытка {}. Будет повторено в следующем цикле",
                    event.getId(), retries);
        }

        outboxRepository.save(event);
    }

    private String determineTopic(OutboxEvent event) {
        return switch (event.getType()) {
            case "TransferCompleted", "TransferRejected" -> accountPaymentTopic;
            case "AccountOpened", "AccountError" -> accountClaimTopic;
            default -> throw new IllegalArgumentException("Неизвестный тип события: " + event.getType());
        };
    }

    private Object deserializePayload(OutboxEvent event) throws Exception {
        return switch (event.getType()) {
            case "TransferCompleted", "TransferRejected" ->
                    objectMapper.readValue(event.getPayload(), TransferResponse.class);
            case "AccountOpened" ->
                    objectMapper.readValue(event.getPayload(), OpenAccountResponseDto.class);
            case "AccountError" ->
                    objectMapper.readValue(event.getPayload(), OpenAccountErrorDto.class);
            default ->
                    throw new IllegalArgumentException("Неизвестный тип события: " + event.getType());
        };
    }
}

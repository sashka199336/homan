package com.globus.financesystem.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus.financesystem.model.entity.OutboxEvent;
import com.globus.financesystem.model.enums.OutboxStatus;
import com.globus.financesystem.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    public void saveEvent(String aggregateType, String aggregateId, String eventType, Object payload) {

            String json = objectMapper.writeValueAsString(payload);

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .type(eventType)
                    .payload(json)
                    .status(OutboxStatus.NEW)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(event);

            log.info("[OutboxService] Событие {} сохранено в outbox (aggregateId={}, type={})",
                    eventType, aggregateId, aggregateType);
    }
}

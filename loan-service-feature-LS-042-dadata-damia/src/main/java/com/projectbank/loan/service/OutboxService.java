package com.projectbank.loan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectbank.loan.dto.LoanRiskDto;
import com.projectbank.loan.entity.LoanApplication;
import com.projectbank.loan.entity.OutboxMessage;
import com.projectbank.loan.exception.OutboxProcessingException;
import com.projectbank.loan.kafka.producer.RiskKafkaProducer;
import com.projectbank.loan.mapper.RiskMapper;
import com.projectbank.loan.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxService {
    private final ObjectMapper objectMapper;
    private final RiskMapper riskMapper;
    private final RiskKafkaProducer riskKafkaProducer;
    private final OutboxMessageRepository outboxMessageRepository;

    @SneakyThrows
    public OutboxMessage buildRiskRequestMessage(LoanApplication entity) {
        LoanRiskDto riskRequestDto = riskMapper.toRiskRequestDto(entity);
        String content = objectMapper.writeValueAsString(riskRequestDto);

        return OutboxMessage.builder()
                .aggregateType("LoanApplication")
                .aggregateId(entity.getId())
                .type("RISK_REQUEST")
                .content(content)
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @SneakyThrows
    public void processMessage(OutboxMessage message) {
        if ("RISK_REQUEST".equals(message.getType())) {
            LoanRiskDto dto = objectMapper.readValue(message.getContent(), LoanRiskDto.class);
            riskKafkaProducer.processRiskRequest(dto);
            message.setProcessed(true);
            outboxMessageRepository.save(message);
            log.info("Отправлен RISK_REQUEST в Kafka: {}", dto.requestID());
        } else {
            throw new OutboxProcessingException("Неизвестный тип сообщения: " + message.getType());
        }
    }
}

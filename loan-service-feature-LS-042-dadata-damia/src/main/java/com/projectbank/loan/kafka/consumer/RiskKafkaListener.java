package com.projectbank.loan.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projectbank.loan.dto.RiskLoanDto;
import com.projectbank.loan.service.LoanDecisionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RiskKafkaListener {

    private final LoanDecisionService loanDecisionService;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.topic.risk-loan}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {

        RiskLoanDto dto = objectMapper.readValue(message, RiskLoanDto.class);
        log.info("[KAFKA] Получен ответ от Risk-сервиса c requestID: {}", dto.requestID());

        loanDecisionService.decideLoanApproval(dto);

        log.info("[KAFKA] Обработано сообщение от Risk-сервиса с requestID : {}", dto.requestID());
    }
}
package com.projectbank.loan.kafka.producer;

import com.projectbank.loan.dto.LoanRiskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiskKafkaProducer {

    private final KafkaTemplate<String, LoanRiskDto> kafkaTemplate;

    @Value("${spring.kafka.topic.loan-risk}")
    private String loanEventsTopic;

    public void processRiskRequest(LoanRiskDto loanRiskDto) {
        kafkaTemplate.send(loanEventsTopic, loanRiskDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[KAFKA] Ошибка при отправке RiskRequestDto в Kafka: {}", ex.getMessage(), ex);
                    } else {
                        log.info("[KAFKA] Успешно отправлено сообщение в Kafka: {}", loanRiskDto.requestID());
                    }
                });
    }
}

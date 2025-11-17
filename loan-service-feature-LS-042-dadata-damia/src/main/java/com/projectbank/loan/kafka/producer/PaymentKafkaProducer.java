package com.projectbank.loan.kafka.producer;

import com.projectbank.loan.dto.LoanPaymentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentKafkaProducer {

    private final KafkaTemplate<String, LoanPaymentDto> kafkaTemplate;

    @Value("${spring.kafka.topic.loan-payment}")
    private String loanEventsTopic;

    public void sendRiskRequest(LoanPaymentDto loanPaymentDto) {
        kafkaTemplate.send(loanEventsTopic, loanPaymentDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[KAFKA] Ошибка при отправке RiskRequestDto в Kafka: {}", ex.getMessage(), ex);
                    } else {
                        log.info("[KAFKA] Успешно отправлено сообщение в Kafka: {}", loanPaymentDto);
                    }
                });
    }
}

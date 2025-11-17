package com.projectbank.loan.kafka.producer;

import com.projectbank.loan.dto.LoanDecisionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClaimKafkaProducer {

    private final KafkaTemplate<String, LoanDecisionDto> kafkaTemplate;

    @Value("${spring.kafka.topic.loan-claim.decision}")
    private String loanEventTopic;

    public void sendClaimRequest(LoanDecisionDto loanDecisionDto) {
        kafkaTemplate.send(loanEventTopic, loanDecisionDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[KAFKA] Ошибка при отправке LoanDecisionDto в Kafka: {}", ex.getMessage(), ex);
                    } else {
                        log.info("[KAFKA] Успешно отправлено сообщение в Kafka: {}", loanDecisionDto);
                    }
                });
    }
}

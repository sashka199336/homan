package com.projectbank.loan.kafka.producer;

import com.projectbank.loan.dto.DamiaRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DamiaKafkaProducer {

    private final KafkaTemplate<String, DamiaRequestDto> kafkaTemplate;

    @Value("${spring.kafka.topic.damia.request}")
    private String topic;

    public void sendDamiaRequest(DamiaRequestDto dto) {
        kafkaTemplate.send(topic, dto.businessId().toString(), dto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[Kafka] Ошибка отправки DamiaRequestDto: {}", ex.getMessage(), ex);
                    } else {
                        log.info("[Kafka] Успешно отправлен DamiaRequestDto: {}", dto);
                    }
                });
    }
}


package com.projectbank.loan.kafka.consumer;

import com.projectbank.loan.dto.DamiaResponseDto;
import com.projectbank.loan.service.DamiaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DamiaKafkaListener {
    private final DamiaService damiaService;

    @KafkaListener(topics = "${spring.kafka.topic.damia-response}", groupId = "loan-service")
    public void listen(DamiaResponseDto damiaResponseDto) {
        log.info("[Kafka] Получен ответ Damia: {}", damiaResponseDto);
        damiaService.processDamiaResponse(damiaResponseDto);
    }
}

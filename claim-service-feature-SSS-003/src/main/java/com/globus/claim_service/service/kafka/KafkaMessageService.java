package com.globus.claim_service.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void sendMessage(String topic, String key, T message) {
        log.info("Sending message to topic {} with key {} and Object type {}", topic, key, message.getClass().getName());
        kafkaTemplate.send(topic, key, message);
    }
}

package com.globus.claim_service.service.kafka.producer;

import com.globus.claim_service.event.NotificationEvent;
import com.globus.claim_service.service.kafka.KafkaMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducerKafkaService {
    private final KafkaMessageService kafkaMessageService;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    public void sendNotification(NotificationEvent event) {
        log.info("Sending Notification to topic {} with notivicationId {}", notificationTopic, event.getNotificationId());
        kafkaMessageService.sendMessage(notificationTopic, event.getNotificationId(), event);
    }
}

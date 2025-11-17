package ru.globus.testproducer.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.globus.testproducer.dto.NotificationDto;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

    @Value("${spring.kafka.topic.notifications}")
    private String notificationsTopic;

    public void sendNotification(NotificationDto notificationDto) {
        log.info("Sending notification to topic {}: {}", notificationsTopic, notificationDto);
        CompletableFuture<?> future = kafkaTemplate.send(
                notificationsTopic,
                notificationDto.getNotificationId(),
                notificationDto
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Notification sent successfully: {}", notificationDto.getNotificationId());
            } else {
                log.error("Failed to send notification: {}", notificationDto.getNotificationId(), ex);
            }
        });
    }
}

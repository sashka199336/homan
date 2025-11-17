package ru.globus.emailnotificationms.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.globus.emailnotificationms.dto.response.NotificationResponse;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailResponseSender {

    private final KafkaTemplate<String, NotificationResponse> kafkaTemplate;

    @Value("${spring.kafka.topic.notification-responses}")
    String responseTopic;

    public void sendResponse(NotificationResponse response) {
        log.info("Sending notification to topic {}: {}", responseTopic, response.getNotificationId());
        CompletableFuture<?> future = kafkaTemplate.send(
                responseTopic,
                response.getNotificationId(),
                response)
                .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent response for notification {}", response.getNotificationId());
                        } else {
                            log.error("Failed to send response for notification {}", response.getNotificationId(), ex);
                        }
                });
    }
}

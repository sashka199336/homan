package ru.globus.notificationsystem.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.globus.notificationsystem.dto.response.NotificationResponse;
import ru.globus.notificationsystem.service.NotificationHistoryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationResponseListener {

    private final NotificationHistoryService notificationHistoryService;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topic.notification-responses}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenNotificationResponses(@Payload NotificationResponse notificationResponse) {
        log.info("Received notification response [{}]", notificationResponse);
        notificationHistoryService.update(notificationResponse);
    }
}

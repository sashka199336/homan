package ru.globus.notificationsystem.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.globus.notificationsystem.dto.request.NotificationDto;
import ru.globus.notificationsystem.service.NotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topic.notifications}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenNotifications(@Payload NotificationDto notificationDto) {
        log.info("Received notification [{}]", notificationDto);
        notificationService.process(
                    notificationDto, notificationDto.getNotificationId());
    }
}

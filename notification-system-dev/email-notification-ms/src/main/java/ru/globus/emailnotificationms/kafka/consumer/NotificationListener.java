package ru.globus.emailnotificationms.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.globus.emailnotificationms.dto.request.EmailNotificationDto;
import ru.globus.emailnotificationms.service.NotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.email-notifications}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenNotifications(@Payload EmailNotificationDto notificationDto) {
        log.info("Received notification [{}]", notificationDto);
        notificationService.process(
                    notificationDto, notificationDto.getNotificationId());
    }
}

package ru.globus.notificationsystem.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.globus.notificationsystem.dto.request.NotificationRuleDto;
import ru.globus.notificationsystem.service.NotificationRuleService;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRuleListener {

    private final NotificationRuleService notificationRuleService;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topic.notifications-rule}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenNotificationsRule(@Payload NotificationRuleDto notificationRuleDto) {
        log.info("Received notification rule[{}]", notificationRuleDto);
         notificationRuleService.save(notificationRuleDto);
    }
}

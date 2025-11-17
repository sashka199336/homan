package ru.globus.testproducer.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.globus.testproducer.dto.NotificationRuleDto;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRuleProducer {

    private final KafkaTemplate<String, NotificationRuleDto> kafkaTemplate;

    @Value("${spring.kafka.topic.notifications-rule}")
    private String notificationRuleTopic;

    public void sendNotificationRule(NotificationRuleDto notificationRuleDto) {
        log.info("Sending notification to topic {}: {}", notificationRuleTopic, notificationRuleDto);
        CompletableFuture<?> future = kafkaTemplate.send(
                notificationRuleTopic,
                notificationRuleDto.getClientId(),
                notificationRuleDto
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Notification rule sent successfully: {}", notificationRuleDto.getClientId());
            } else {
                log.error("Failed to send notification rule: {}", notificationRuleDto.getClientId(), ex);
            }
        });
    }
}

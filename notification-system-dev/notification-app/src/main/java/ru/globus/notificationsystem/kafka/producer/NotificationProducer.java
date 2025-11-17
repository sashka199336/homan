package ru.globus.notificationsystem.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.globus.notificationsystem.entity.Channel;
import ru.globus.notificationsystem.dto.request.ChannelNotificationDto;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, ChannelNotificationDto> kafkaTemplate;

    @Value("${spring.kafka.producer.topic.email-notifications}")
    private String emailNotificationsTopic;

    @Value("${spring.kafka.producer.topic.push-notifications}")
    private String pushNotificationsTopic;

    @Value("${spring.kafka.producer.topic.sms-notifications}")
    private String smsNotificationsTopic;

    @Value("${spring.kafka.producer.topic.web-notifications}")
    private String webNotificationsTopic;

    private String notificationsTopic;

    public void sendNotification(ChannelNotificationDto channelNotificationDto, Channel channel) {
        determineNotificationsTopic(channel);
        log.info("Sending notification to topic {}: {}", notificationsTopic, channelNotificationDto.getNotificationId());
        CompletableFuture<?> future = kafkaTemplate.send(
                notificationsTopic,
                channelNotificationDto.getNotificationId(),
                channelNotificationDto
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Notification sent successfully: {}", channelNotificationDto.getNotificationId());
            } else {
                log.error("Failed to send notification: {}", channelNotificationDto.getNotificationId(), ex);
            }
        });
    }

    private void determineNotificationsTopic (Channel channel) {
        switch (channel) {
            case EMAIL -> notificationsTopic = emailNotificationsTopic;
            case SMS -> notificationsTopic = smsNotificationsTopic;
            case PUSH -> notificationsTopic = pushNotificationsTopic;
            case WEB -> notificationsTopic = webNotificationsTopic;
        }
    }
}

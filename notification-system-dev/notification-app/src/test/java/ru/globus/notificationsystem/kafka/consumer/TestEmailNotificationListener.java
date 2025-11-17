package ru.globus.notificationsystem.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.globus.notificationsystem.dto.request.EmailNotificationDto;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class TestEmailNotificationListener {

    private final BlockingQueue<EmailNotificationDto> notifications = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${spring.kafka.topic.email-notifications}",
            groupId = "test-group",
            properties = {
                    "spring.json.trusted.packages=ru.globus.notificationsystem.dto.request",
                    "spring.json.type.mapping=EmailNotificationDto: ru.globus.notificationsystem.dto.request.EmailNotificationDto"
            }
    )
    public void listen(EmailNotificationDto dto) {
        notifications.add(dto);
    }

    public EmailNotificationDto getResponse() throws InterruptedException {
        return notifications.poll(30, TimeUnit.SECONDS);
    }
}

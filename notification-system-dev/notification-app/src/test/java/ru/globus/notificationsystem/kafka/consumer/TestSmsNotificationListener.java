package ru.globus.notificationsystem.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.globus.notificationsystem.dto.request.SmsNotificationDto;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class TestSmsNotificationListener {

    private final BlockingQueue<SmsNotificationDto> notifications = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${spring.kafka.topic.sms-notifications}",
            groupId = "test-group",
            properties = {
                    "spring.json.trusted.packages=ru.globus.notificationsystem.dto.request",
                    "spring.json.type.mapping=SmsNotificationDto: ru.globus.notificationsystem.dto.request.SmsNotificationDto"
            }
    )
    public void listen(SmsNotificationDto dto) {
        notifications.add(dto);
    }

    public SmsNotificationDto getResponse() throws InterruptedException {
        return notifications.poll(30, TimeUnit.SECONDS);
    }
}

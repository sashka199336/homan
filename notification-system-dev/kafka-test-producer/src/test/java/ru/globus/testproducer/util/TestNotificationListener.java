package ru.globus.testproducer.util;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.globus.testproducer.dto.NotificationDto;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class TestNotificationListener {

    private static final int TIME_OUT = 10;
    private final BlockingQueue<NotificationDto> notifications = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${spring.kafka.topic.notifications}",
            groupId = "test-group",
            properties = {
                    "value.deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
                    "spring.json.add.type.headers= true",
                    "spring.json.trusted.packages= ru.globus.testproducer.dto",
                    "spring.json.type.mapping=NotificationDto: ru.globus.testproducer.dto.NotificationDto"
            }
    )
    public void listen(NotificationDto dto) {
        notifications.add(dto);
    }

    public NotificationDto getResponse() throws InterruptedException {
        return notifications.poll(TIME_OUT, TimeUnit.SECONDS);
    }
}

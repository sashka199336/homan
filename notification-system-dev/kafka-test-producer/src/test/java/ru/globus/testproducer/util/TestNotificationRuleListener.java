package ru.globus.testproducer.util;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.globus.testproducer.dto.NotificationRuleDto;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class TestNotificationRuleListener {

    private static final int TIME_OUT = 10;
    private final BlockingQueue<NotificationRuleDto> responses = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${spring.kafka.topic.notifications-rule}",
            groupId = "test-group",
            properties = {
                    "value.deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
                    "spring.json.add.type.headers= true",
                    "spring.json.trusted.packages=ru.globus.testproducer.dto",
                    "spring.json.type.mapping=NotificationRuleDto:ru.globus.testproducer.dto.NotificationRuleDto"
            }
    )
    public void listen(NotificationRuleDto notificationRuleDto) {
        responses.add(notificationRuleDto);
    }

    public NotificationRuleDto getResponse() throws InterruptedException {
        return responses.poll(TIME_OUT, TimeUnit.SECONDS);
    }
}

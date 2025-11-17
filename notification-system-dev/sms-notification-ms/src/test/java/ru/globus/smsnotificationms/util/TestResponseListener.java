package ru.globus.smsnotificationms.util;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.globus.smsnotificationms.dto.response.NotificationResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class TestResponseListener {

    private final BlockingQueue<NotificationResponse> responses = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${spring.kafka.topic.notification-responses}",
            groupId = "test-group",
            properties = {
                    "spring.json.trusted.packages=ru.globus.smsnotificationms.dto.response",
                    "spring.json.type.mapping=NotificationResponse:ru.globus.smsnotificationms.dto.response.NotificationResponse"
            }
    )
    public void listen(NotificationResponse response) {
        responses.add(response);
    }

    public NotificationResponse getResponse() throws InterruptedException {
        return responses.poll(30, TimeUnit.SECONDS);
    }
}


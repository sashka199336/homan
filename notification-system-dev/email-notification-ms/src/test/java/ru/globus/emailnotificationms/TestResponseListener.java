package ru.globus.emailnotificationms;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.globus.emailnotificationms.dto.response.NotificationResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class TestResponseListener {

    private final BlockingQueue<NotificationResponse> responses = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${spring.kafka.topic.notification-responses}",
            groupId = "test-group",
            properties = {
                    "spring.json.trusted.packages=ru.globus.emailnotificationms.dto.response",
                    "spring.json.type.mapping=NotificationResponse:ru.globus.emailnotificationms.dto.response.NotificationResponse"
            }
    )
    public void listen(NotificationResponse response) {
        responses.add(response);
    }

    public NotificationResponse getResponse() throws InterruptedException {
        return responses.poll(30, TimeUnit.SECONDS);
    }
}

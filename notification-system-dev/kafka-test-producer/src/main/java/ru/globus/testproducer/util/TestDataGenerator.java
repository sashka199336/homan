package ru.globus.testproducer.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.globus.testproducer.dto.Channel;
import ru.globus.testproducer.dto.NotificationDto;
import ru.globus.testproducer.dto.NotificationRuleDto;
import ru.globus.testproducer.dto.SenderDto;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataGenerator {

    public NotificationDto generateNotification() {
        NotificationDto notification = new NotificationDto();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setCreatedAt(LocalDateTime.now());
        SenderDto sender = new SenderDto();
        sender.setSystem("test-system");
        sender.setUserId("test-user-id");
        notification.setSender(sender);
        notification.setClientId("test-client-id");
        notification.setMessage("test-message");

        return notification;
    }

    public NotificationRuleDto generateNotificationRule() {
        NotificationRuleDto notificationRule = new NotificationRuleDto();
        notificationRule.setClientId("test-client-id");
        notificationRule.setEmail("test@test.com");
        notificationRule.setPhone("79998765432");
        notificationRule.setPreferNotificationChannels(Set.of(Channel.EMAIL, Channel.SMS));

        return notificationRule;
    }
}

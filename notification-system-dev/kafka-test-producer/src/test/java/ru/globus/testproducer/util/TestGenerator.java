package ru.globus.testproducer.util;

import ru.globus.testproducer.dto.Channel;
import ru.globus.testproducer.dto.NotificationDto;
import ru.globus.testproducer.dto.NotificationRuleDto;
import ru.globus.testproducer.dto.SenderDto;
import java.time.LocalDateTime;
import java.util.Set;

public class TestGenerator {

    public static final String TEST_CLIENT_ID = "test-client-id";
    public static final String TEST_NOTIFICATION_ID = "test-notification-id";
    private static final String TEST_SYSTEM = "test-system";
    private static final String TEST_USER_ID = "test-user-id";
    private static final String TEST_MESSAGE = "test-message";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PHONE = "79998765432";

    public NotificationDto generateNotificationDto() {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotificationId(TEST_NOTIFICATION_ID);
        notificationDto.setCreatedAt(LocalDateTime.now());
        SenderDto sender = new SenderDto();
        sender.setSystem(TEST_SYSTEM);
        sender.setUserId(TEST_USER_ID);
        notificationDto.setSender(sender);
        notificationDto.setClientId(TEST_CLIENT_ID);
        notificationDto.setMessage(TEST_MESSAGE);
        return notificationDto;
    }

    public NotificationRuleDto generateNotificationRuleDto() {
        NotificationRuleDto notificationRuleDto = new NotificationRuleDto();
        notificationRuleDto.setClientId(TEST_CLIENT_ID);
        notificationRuleDto.setEmail(TEST_EMAIL);
        notificationRuleDto.setPhone(TEST_PHONE);
        notificationRuleDto.setPreferNotificationChannels(Set.of(Channel.EMAIL, Channel.SMS));
        return notificationRuleDto;
    }
}

package ru.globus.notificationsystem.util;

import ru.globus.notificationsystem.dto.request.*;
import ru.globus.notificationsystem.dto.response.NotificationResponse;
import ru.globus.notificationsystem.entity.Channel;
import ru.globus.notificationsystem.entity.NotificationHistory;
import ru.globus.notificationsystem.entity.NotificationRule;
import ru.globus.notificationsystem.entity.Status;
import java.time.LocalDateTime;
import java.util.Set;

public class TestGenerator {

    private static final String TEST_NOTIFICATION_ID = "test-notification-id";
    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_MESSAGE = "Test message";
    private static final String TEST_SENDER_SYSTEM = "Test System";
    private static final String TEST_SENDER_USER_ID = "test-sender-user-id";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PHONE = "79998765432";

    public NotificationRule generateNotificationRule() {
        NotificationRule notificationRule = new NotificationRule();
        notificationRule.setClientId(TEST_CLIENT_ID);
        notificationRule.setEmail(TEST_EMAIL);
        notificationRule.setPhone(TEST_PHONE);
        notificationRule.setSmsActive(true);
        notificationRule.setEmailActive(true);
        return notificationRule;
    }

    public NotificationDto generateNotificationDto() {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotificationId(TEST_NOTIFICATION_ID);
        notificationDto.setClientId(TEST_CLIENT_ID);
        notificationDto.setCreatedAt(LocalDateTime.now());
        SenderDto senderDto = new SenderDto();
        senderDto.setSystem(TEST_SENDER_SYSTEM);
        senderDto.setUserId(TEST_SENDER_USER_ID);
        notificationDto.setSender(senderDto);
        notificationDto.setMessage(TEST_MESSAGE);
        return notificationDto;
    }

    public NotificationDto generateNotificationDtoWithNullClient() {
        NotificationDto notificationDtoNullClient = new NotificationDto();
        notificationDtoNullClient.setNotificationId(TEST_NOTIFICATION_ID);
        notificationDtoNullClient.setClientId(null);
        notificationDtoNullClient.setCreatedAt(LocalDateTime.now());
        SenderDto senderDto = new SenderDto();
        senderDto.setSystem(TEST_SENDER_SYSTEM);
        senderDto.setUserId(TEST_SENDER_USER_ID);
        notificationDtoNullClient.setSender(senderDto);
        notificationDtoNullClient.setMessage(TEST_MESSAGE);
        return notificationDtoNullClient;
    }

    public EmailNotificationDto generateEmailNotificationDtoExpected() {
        EmailNotificationDto emailExpected = new EmailNotificationDto();
        emailExpected.setEmail(TEST_EMAIL);
        emailExpected.setNotificationId(TEST_NOTIFICATION_ID);
        SenderDto senderDto = new SenderDto();
        senderDto.setSystem(TEST_SENDER_SYSTEM);
        senderDto.setUserId(TEST_SENDER_USER_ID);
        emailExpected.setSender(senderDto);
        emailExpected.setMessage(TEST_MESSAGE);
        return emailExpected;
    }

    public SmsNotificationDto generateSmsNotificationDtoExpected() {
        SmsNotificationDto smsExpected = new SmsNotificationDto();
        smsExpected.setPhone(TEST_PHONE);
        smsExpected.setNotificationId(TEST_NOTIFICATION_ID);
        SenderDto senderDto = new SenderDto();
        senderDto.setSystem(TEST_SENDER_SYSTEM);
        senderDto.setUserId(TEST_SENDER_USER_ID);
        smsExpected.setSender(senderDto);
        smsExpected.setMessage(TEST_MESSAGE);
        return smsExpected;
    }

    public NotificationResponse generateValidNotificationResponse() {
        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setNotificationId(TEST_NOTIFICATION_ID);
        notificationResponse.setChannel("EMAIL");
        notificationResponse.setStatus("SUCCESS");
        return notificationResponse;
    }

    public NotificationResponse generateInvalidNotificationResponse() {
        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setNotificationId(TEST_NOTIFICATION_ID);
        notificationResponse.setChannel("SMS");
        notificationResponse.setStatus(null);
        return notificationResponse;
    }

    public NotificationHistory generateNotificationHistoryEmail() {
        NotificationHistory notificationHistory = new NotificationHistory();
        notificationHistory.setNotificationId(TEST_NOTIFICATION_ID);
        notificationHistory.setChannel(Channel.EMAIL);
        notificationHistory.setStatus(Status.IN_PROGRESS);
        return notificationHistory;
    }

    public NotificationHistory generateNotificationHistorySms() {
        NotificationHistory notificationHistory = new NotificationHistory();
        notificationHistory.setNotificationId(TEST_NOTIFICATION_ID);
        notificationHistory.setChannel(Channel.SMS);
        notificationHistory.setStatus(Status.IN_PROGRESS);
        return notificationHistory;
    }

    public NotificationRuleDto generateNotificationRuleDtoValid() {
        NotificationRuleDto notificationRuleDto = new NotificationRuleDto();
        notificationRuleDto.setClientId(TEST_CLIENT_ID);
        notificationRuleDto.setEmail(TEST_EMAIL);
        notificationRuleDto.setPhone(TEST_PHONE);
        notificationRuleDto.setPreferNotificationChannels(Set.of(Channel.EMAIL, Channel.SMS));
        return notificationRuleDto;
    }
}

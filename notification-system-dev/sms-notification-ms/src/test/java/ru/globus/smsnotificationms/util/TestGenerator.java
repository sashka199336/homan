package ru.globus.smsnotificationms.util;

import ru.globus.smsnotificationms.dto.request.SenderDto;
import ru.globus.smsnotificationms.dto.request.SmsNotificationDto;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestGenerator {

    private static final String TEST_MESSAGE = "Test_message";
    private static final String TEST_PHONE = "79998765432";
    private static final String TEST_SMS_PATH = "sms/send";
    private static final String TEST_SMS_KEY = "12345678-1234-1234-1234-123456789012";
    private static final String TEST_SMS_JSON = "1";
    private static final String TEST_SMS_TEST = "1";

    public SmsNotificationDto generateNotificationDto() {
        SmsNotificationDto notificationDto = new SmsNotificationDto();
        notificationDto.setNotificationId(UUID.randomUUID().toString());
        notificationDto.setCreatedAt(LocalDateTime.now());
        SenderDto sender = new SenderDto();
        sender.setSystem("Test System");
        sender.setUserId("user123");
        notificationDto.setSender(sender);
        notificationDto.setMessage(TEST_MESSAGE);
        notificationDto.setPhone(TEST_PHONE);
        return notificationDto;
    }

    public String generateUrlWithParameters(String phone) {
        return "/" +
                TEST_SMS_PATH +
                "?api_id=" +
                TEST_SMS_KEY +
                "&to=" +
                phone +
                "&msg=" +
                TEST_MESSAGE +
                "&json=" +
                TEST_SMS_JSON +
                "&test=" +
                TEST_SMS_TEST;
    }

    public String generateSmsResponseOk() {
        return """
                {
                    "status": "OK",
                    "status_code": 100,
                    "sms": {
                        "79998765432": {
                            "status": "OK",
                            "status_code": 100,
                            "sms_id": "000001"
                        }
                    },
                    "balance": 4122.56
                }""";
    }

    public String generateSmsResponseError() {
        return """
                {
                    "status": "OK",
                    "status_code": 202,
                    "sms": {
                        "": {
                            "status": "ERROR",
                            "status_code": 202,
                            "sms_id": "000001"
                        }
                    },
                    "balance": 4122.56
                }""";
    }
}

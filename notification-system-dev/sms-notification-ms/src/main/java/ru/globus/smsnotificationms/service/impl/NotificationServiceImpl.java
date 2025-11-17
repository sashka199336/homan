package ru.globus.smsnotificationms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.globus.smsnotificationms.config.SmsProperties;
import ru.globus.smsnotificationms.dto.request.SmsNotificationDto;
import ru.globus.smsnotificationms.dto.response.NotificationResponse;
import ru.globus.smsnotificationms.dto.response.SmsResponse;
import ru.globus.smsnotificationms.exception.NotificationException;
import ru.globus.smsnotificationms.exception.SmsException;
import ru.globus.smsnotificationms.kafka.producer.SmsResponseSender;
import ru.globus.smsnotificationms.service.NotificationService;
import ru.globus.smsnotificationms.service.SmsServiceClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SmsServiceClient smsServiceClient;
    private final SmsProperties smsProperties;
    private final SmsResponseSender responseSender;

    @Override
    public void process(SmsNotificationDto notificationDto, String notificationId) {
        log.info("Processing notification with ID: {}", notificationDto.getNotificationId());
        NotificationResponse response = new NotificationResponse();
        response.setId(UUID.randomUUID().toString());
        response.setNotificationId(notificationId);
        response.setChannel("SMS");
        try {
            sendNotification(notificationDto);
            response.setStatus("SUCCESS");
            responseSender.sendResponse(response);
        } catch (NotificationException e) {
            log.error("Notification processing failed: {}", e.getMessage(), e);
            response.setStatus("ERROR");
            response.setErrorCode("PROCESS_ERROR");
            response.setErrorMessage("Notification processing failed: " + e.getMessage());
            responseSender.sendResponse(response);
        } catch (Exception e) {
            log.error("Unexpected error during notification processing: {}", e.getMessage(), e);
            response.setStatus("ERROR");
            response.setErrorCode("UNEXPECTED_ERROR");
            response.setErrorMessage("Unexpected error occurred: " + e.getMessage());
            responseSender.sendResponse(response);
        }
    }

    private void sendNotification(SmsNotificationDto notificationDto) {
        HashMap<String, String> propertiesMap = smsProperties.getSmsProperties();
        String phoneNumber = getPhoneNumber(notificationDto, propertiesMap);
        log.info("Sending SMS to: {}", phoneNumber);
        try {
            SmsResponse response = smsServiceClient.sendSms(
                    propertiesMap.get("key"),
                    phoneNumber,
                    abbreviate(notificationDto.getMessage(), Integer.parseInt(propertiesMap.get("maxLength"))),
                    propertiesMap.getOrDefault("json", "1"),
                    propertiesMap.getOrDefault("test", "0")
            );
            handleResponse(response, phoneNumber);
        } catch (FeignException e) {
            handleFeignException(e);
        } catch (Exception e) {
            log.error("Unexpected error during SMS sending", e);
            throw new SmsException("Unexpected error: " + e.getMessage());
        }
    }

    private String getPhoneNumber(SmsNotificationDto notificationDto, HashMap<String, String> propertiesMap) {
        return (propertiesMap.get("to") != null
                && !(propertiesMap.get("to")).isBlank())
                ? sanitizePhoneNumber(propertiesMap.get("to"))
                : sanitizePhoneNumber(notificationDto.getPhone());
    }

    private void handleResponse(SmsResponse response, String phoneNumber) {
        if (response == null) {
            throw new SmsException("Empty response from SMS service");
        }

        if (response.statusCode() != 100) {
            throw new SmsException("SMS sending failed: " +
                    (response.sms().get("status_text") != null ? response.sms().get("status_text") : "Unknown error"));
        }

        log.info("SMS successfully sent to {}", phoneNumber);
    }

    private void handleFeignException(FeignException e) {
        if (e.contentUTF8() != null) {
            try {
                SmsResponse errorResponse = new ObjectMapper()
                        .readValue(e.contentUTF8(), SmsResponse.class);
                throw new SmsException("SMS service error: " + errorResponse.sms().get("status_text"));
            } catch (IOException ex) {
                throw new SmsException("Failed to parse error response: " + e.contentUTF8());
            }
        }
        throw new SmsException("SMS service communication error: " + e.getMessage());
    }

    private String sanitizePhoneNumber(String phone) {
        return phone.replaceAll("[^0-9+]", "");
    }

    private String abbreviate(String str, int maxLength) {
        return str != null && str.length() > maxLength
                ? str.substring(0, maxLength) + "..."
                : str;
    }
}

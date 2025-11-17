package ru.globus.emailnotificationms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.globus.emailnotificationms.config.EmailProperties;
import ru.globus.emailnotificationms.dto.request.EmailNotificationDto;
import ru.globus.emailnotificationms.dto.response.NotificationResponse;
import ru.globus.emailnotificationms.exception.NotificationException;
import ru.globus.emailnotificationms.kafka.producer.EmailResponseSender;
import ru.globus.emailnotificationms.service.NotificationService;
import java.util.HashMap;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;
    private final EmailProperties emailProperties;
    private final SimpleMailMessage simpleMailMessage;
    private final EmailResponseSender responseSender;

    @Override
    public void process(EmailNotificationDto notificationDto, String notificationId) {
        log.info("Processing notification with ID: {}", notificationDto.getNotificationId());
        NotificationResponse response = new NotificationResponse();
                response.setId(UUID.randomUUID().toString());
                response.setNotificationId(notificationId);
                response.setChannel("EMAIL");
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

    private void sendNotification(EmailNotificationDto notificationDto) throws NotificationException {
        log.info("Sending EMAIL for notificationId {} ", notificationDto.getNotificationId());
        setSimpleMailMessage(notificationDto);
        try {
            javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
            log.error("Email sending failed for notification {}: {}",
                    notificationDto.getNotificationId(), e.getMessage());
            throw new NotificationException("Unexpected error occurred: " + e.getMessage()) {
            };
        }
    }

    private void setSimpleMailMessage(EmailNotificationDto notificationDto) {
        log.info("Set SimpleMailMessage {} ", notificationDto.getNotificationId());
        HashMap<String, String> propertiesMap = emailProperties.getMailProperties();
        simpleMailMessage.setFrom(propertiesMap.get("mail-from"));
        String emailAddress = getEmailAAddress(notificationDto, propertiesMap);
        simpleMailMessage.setTo(emailAddress);
        simpleMailMessage.setSubject(propertiesMap.get("subject") +
                notificationDto.getSender().getSystem());
        simpleMailMessage.setText(notificationDto.getMessage());
    }

    private String getEmailAAddress(EmailNotificationDto notificationDto, HashMap<String, String> propertiesMap) {
        return (propertiesMap.get("mail-to") != null
                && propertiesMap.get("mail-to").contains("@"))
                ? propertiesMap.get("mail-to")
                : notificationDto.getEmail();
    }
}

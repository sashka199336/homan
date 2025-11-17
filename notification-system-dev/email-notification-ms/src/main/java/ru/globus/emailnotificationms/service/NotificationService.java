package ru.globus.emailnotificationms.service;

import ru.globus.emailnotificationms.dto.request.EmailNotificationDto;

public interface NotificationService {

    void process(EmailNotificationDto notificationDto, String notificationId);
}

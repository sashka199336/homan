package ru.globus.smsnotificationms.service;

import ru.globus.smsnotificationms.dto.request.SmsNotificationDto;

public interface NotificationService {

    void process(SmsNotificationDto notificationDto, String notificationId);
}


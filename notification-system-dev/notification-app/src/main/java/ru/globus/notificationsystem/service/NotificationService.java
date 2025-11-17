package ru.globus.notificationsystem.service;

import ru.globus.notificationsystem.dto.request.NotificationDto;

public interface NotificationService {

    void process(NotificationDto notificationDto, String notificationId);
}

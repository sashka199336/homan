package ru.globus.testproducer.service;

import ru.globus.testproducer.dto.NotificationDto;

public interface NotificationService {

    void sendNotification(NotificationDto notificationDto);
    void sendGeneratedNotification();
}

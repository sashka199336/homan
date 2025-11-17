package ru.globus.notificationsystem.service;

import ru.globus.notificationsystem.dto.request.NotificationDto;
import ru.globus.notificationsystem.dto.response.NotificationResponse;
import ru.globus.notificationsystem.entity.Channel;
import ru.globus.notificationsystem.entity.NotificationRule;

public interface NotificationHistoryService {

    void save(NotificationDto notificationDto,
         NotificationRule notificationRule,
         Channel channel);

    void update(NotificationResponse notificationResponse);
}

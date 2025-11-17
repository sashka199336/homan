package ru.globus.notificationsystem.service;

import ru.globus.notificationsystem.dto.request.NotificationRuleDto;
import ru.globus.notificationsystem.entity.NotificationRule;

public interface NotificationRuleService {

    NotificationRule getNotificationRuleByClientId(String clientId);

    void save(NotificationRuleDto notificationRuleDto);
}

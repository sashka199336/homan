package ru.globus.testproducer.service;

import ru.globus.testproducer.dto.NotificationRuleDto;

public interface NotificationRuleService {

    void sendNotificationRule(NotificationRuleDto notificationRuleDto);
    void sendGeneratedNotificationRule();
}

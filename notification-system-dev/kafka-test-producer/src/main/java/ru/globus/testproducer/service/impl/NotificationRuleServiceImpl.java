package ru.globus.testproducer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.globus.testproducer.dto.NotificationRuleDto;
import ru.globus.testproducer.kafka.NotificationRuleProducer;
import ru.globus.testproducer.service.NotificationRuleService;
import ru.globus.testproducer.util.TestDataGenerator;

@Service
@RequiredArgsConstructor
public class NotificationRuleServiceImpl implements NotificationRuleService {

    private final NotificationRuleProducer notificationRuleProducer;
    private final TestDataGenerator testDataGenerator;

    @Override
    public void sendNotificationRule(NotificationRuleDto notificationRuleDto) {
        notificationRuleProducer.sendNotificationRule(notificationRuleDto);
    }

    @Override
    public void sendGeneratedNotificationRule() {
        notificationRuleProducer.sendNotificationRule(testDataGenerator.generateNotificationRule());

    }
}

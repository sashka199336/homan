package ru.globus.testproducer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.globus.testproducer.dto.NotificationDto;
import ru.globus.testproducer.kafka.NotificationProducer;
import ru.globus.testproducer.service.NotificationService;
import ru.globus.testproducer.util.TestDataGenerator;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationProducer notificationProducer;
    private final TestDataGenerator testDataGenerator;

    @Override
    public void sendNotification(NotificationDto notificationDto) {
        notificationProducer.sendNotification(notificationDto);
    }

    @Override
    public void sendGeneratedNotification() {
        notificationProducer.sendNotification(testDataGenerator.generateNotification());
    }
}

package ru.globus.notificationsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.globus.notificationsystem.dto.request.NotificationDto;
import ru.globus.notificationsystem.dto.response.NotificationResponse;
import ru.globus.notificationsystem.entity.Channel;
import ru.globus.notificationsystem.entity.NotificationHistory;
import ru.globus.notificationsystem.entity.NotificationRule;
import ru.globus.notificationsystem.entity.Status;
import ru.globus.notificationsystem.repository.NotificationHistoryRepository;
import ru.globus.notificationsystem.service.NotificationHistoryService;
import ru.globus.notificationsystem.util.mapper.NotificationHistoryMapper;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationHistoryServiceImpl implements NotificationHistoryService {

    private final NotificationHistoryRepository notificationHistoryRepository;
    private final NotificationHistoryMapper notificationHistoryMapper;

    @Transactional
    @Override
    public void save(NotificationDto notificationDto,
                     NotificationRule notificationRule,
                     Channel channel) {
        log.info("Saving history:  channel {}, notificationID: {}", channel, notificationDto.getNotificationId());
        NotificationHistory notificationHistory = notificationHistoryMapper.toEntity(
                notificationDto,
                notificationRule,
                channel);
        Optional<NotificationHistory> notificationHistoryPresent = notificationHistoryRepository
                .findByNotificationIdAndChannel(notificationHistory.getNotificationId(), notificationHistory.getChannel());
        notificationHistoryPresent.ifPresent(history -> notificationHistory.setId(history.getId()));
        notificationHistoryRepository.save(notificationHistory);
    }

    @Transactional
    @Override
    public void update(NotificationResponse notificationResponse) {
        log.info("Update history:  channel {}, notificationID: {}",
                notificationResponse.getChannel(), notificationResponse.getNotificationId());

        Channel channel = Channel.valueOf(notificationResponse.getChannel());
        String notificationId = notificationResponse.getNotificationId();

        NotificationHistory notificationHistory = notificationHistoryRepository
                .findByNotificationIdAndChannel(notificationId, channel)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Failed to update a non-existent history:  channel " + channel +
                        ", notificationID " + notificationResponse.getNotificationId()));

        notificationHistory.setStatus(
                (notificationResponse.getStatus() != null &&
                        Arrays.stream(Status.values())
                                .anyMatch(e -> e.name().equalsIgnoreCase(notificationResponse.getStatus())))
                        ? Status.valueOf(notificationResponse.getStatus().toUpperCase())
                        : Status.ERROR
        );
        notificationHistoryRepository.save(notificationHistory);
    }
}

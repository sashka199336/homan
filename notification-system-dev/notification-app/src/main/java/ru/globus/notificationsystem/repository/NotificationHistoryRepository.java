package ru.globus.notificationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.globus.notificationsystem.entity.NotificationHistory;
import ru.globus.notificationsystem.entity.Channel;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, UUID> {

    Optional<NotificationHistory> findByNotificationIdAndChannel(String notificationId, Channel channel);
}

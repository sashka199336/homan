package ru.globus.notificationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.globus.notificationsystem.entity.NotificationRule;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRuleRepository extends JpaRepository<NotificationRule, UUID> {

    Optional<NotificationRule> findByClientId(String clientId);

}

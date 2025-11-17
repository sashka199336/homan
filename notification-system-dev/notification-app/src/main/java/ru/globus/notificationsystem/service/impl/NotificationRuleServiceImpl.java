package ru.globus.notificationsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.globus.notificationsystem.dto.request.NotificationRuleDto;
import ru.globus.notificationsystem.entity.NotificationRule;
import ru.globus.notificationsystem.repository.NotificationRuleRepository;
import ru.globus.notificationsystem.service.NotificationRuleService;
import ru.globus.notificationsystem.util.mapper.NotificationRuleMapper;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRuleServiceImpl implements NotificationRuleService {

    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationRuleMapper notificationRuleMapper;

    @Transactional(readOnly = true)
    @Override
    public NotificationRule getNotificationRuleByClientId(String clientId) {
        log.info("Get NotificationRule by ClientId: {}", clientId);
        return notificationRuleRepository
                .findByClientId(clientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "NotificationRule is not found. ClientID = " + clientId));
    }

    @Transactional
    @Override
    public void save(NotificationRuleDto notificationRuleDto) {
        log.info("Saving notificationRule for client with ID: {}", notificationRuleDto.getClientId());
        NotificationRule notificationRuleNew = notificationRuleMapper.toEntity(notificationRuleDto);
        Optional<NotificationRule> notificationRuleCurrent = notificationRuleRepository
                .findByClientId(notificationRuleDto.getClientId());
        notificationRuleCurrent.ifPresent(notificationRule -> {
                    notificationRuleNew.setId(notificationRule.getId());
                    notificationRuleNew.setCreateDateTime(notificationRule.getCreateDateTime());
                    }
                );
        notificationRuleRepository.save(notificationRuleNew);
    }
}

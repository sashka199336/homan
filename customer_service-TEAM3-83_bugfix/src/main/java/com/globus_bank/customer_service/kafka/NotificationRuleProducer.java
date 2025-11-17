package com.globus_bank.customer_service.kafka;

import com.globus_bank.customer_service.dto.common.ContactsDto;
import com.globus_bank.customer_service.dto.kafka.NotificationRuleDto;
import com.globus_bank.customer_service.utils.mapper.dto.NotificationRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class NotificationRuleProducer {

    private final KafkaTemplate<String, NotificationRuleDto> kafkaTemplate;

    @Value("${spring.kafka.topic.notifications-rule}")
    private String notificationRuleTopic;

    private final NotificationRuleMapper notificationRuleMapper;

    public void sendNotificationRule(List<ContactsDto> contactsDtos) {
        contactsDtos.forEach(contactsDto -> {
            NotificationRuleDto notificationRuleDto = notificationRuleMapper.toNotificationRuleDto(contactsDto);

            CompletableFuture<?> future = kafkaTemplate.send(
                    notificationRuleTopic,
                    notificationRuleDto.getClientId(),
                    notificationRuleDto
            );
        });
    }
}

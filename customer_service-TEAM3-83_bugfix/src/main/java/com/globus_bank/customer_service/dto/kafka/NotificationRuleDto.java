package com.globus_bank.customer_service.dto.kafka;

import com.globus_bank.customer_service.entity.enums.Channel;
import lombok.Data;
import java.util.Set;

@Data
public class NotificationRuleDto {

    private String clientId;

    private String email;

    private String phone;

    private Set<Channel> preferNotificationChannels;
}

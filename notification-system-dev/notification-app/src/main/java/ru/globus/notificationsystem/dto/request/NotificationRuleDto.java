package ru.globus.notificationsystem.dto.request;

import lombok.Data;
import ru.globus.notificationsystem.entity.Channel;
import java.util.Set;

@Data
public class NotificationRuleDto {

        private String clientId;
        private String email;
        private String phone;
        private Set<Channel> preferNotificationChannels;
}

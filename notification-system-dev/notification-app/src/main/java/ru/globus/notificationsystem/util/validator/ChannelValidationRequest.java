package ru.globus.notificationsystem.util.validator;

import ru.globus.notificationsystem.entity.Channel;
import ru.globus.notificationsystem.entity.NotificationRule;

public record ChannelValidationRequest(Channel channel, NotificationRule rule) {
}

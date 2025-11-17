package ru.globus.notificationsystem.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.globus.notificationsystem.dto.request.NotificationRuleDto;
import ru.globus.notificationsystem.entity.Channel;
import ru.globus.notificationsystem.entity.NotificationRule;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface NotificationRuleMapper {

    @Mapping(target = "emailActive", source = "preferNotificationChannels", qualifiedByName = "mapEmailActive")
    @Mapping(target = "smsActive", source = "preferNotificationChannels", qualifiedByName = "mapSmsActive")
    @Mapping(target = "pushActive", source = "preferNotificationChannels", qualifiedByName = "mapPushActive")
    NotificationRule toEntity(NotificationRuleDto notificationRuleDto);

    @Named("mapEmailActive")
    default boolean mapEmailActive(Set<Channel> channels) {
        return channels != null && channels.contains(Channel.EMAIL);
    }

    @Named("mapSmsActive")
    default boolean mapSmsActive(Set<Channel> channels) {
        return channels != null && channels.contains(Channel.SMS);
    }

    @Named("mapPushActive")
    default boolean mapPushActive(Set<Channel> channels) {
        return channels != null && channels.contains(Channel.PUSH);
    }
}

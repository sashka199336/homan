package ru.globus.notificationsystem.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.globus.notificationsystem.dto.request.NotificationDto;
import ru.globus.notificationsystem.entity.Channel;
import ru.globus.notificationsystem.entity.NotificationHistory;
import ru.globus.notificationsystem.entity.NotificationRule;
import ru.globus.notificationsystem.entity.Status;

@Mapper(componentModel = "spring")
public interface NotificationHistoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDateTime", ignore = true)
    @Mapping(target = "updateDateTime", ignore = true)
    @Mapping(target = "notificationId", source = "notificationDto.notificationId")
    @Mapping(target = "channel", source = "channel")
    @Mapping(target = "status", constant = "IN_PROGRESS")
    @Mapping(target = "notificationRule", source = "notificationRule")
    NotificationHistory toEntity(NotificationDto notificationDto,
                                 NotificationRule notificationRule,
                                 Channel channel);

    @Named("channelToString")
    default String channelToString(Channel channel) {
        return channel != null ? channel.name() : null;
    }

    @Named("statusToString")
    default String statusToString(Status status) {
        return status != null ? status.name() : null;
    }
}

package ru.globus.notificationsystem.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.globus.notificationsystem.dto.request.*;
import ru.globus.notificationsystem.entity.NotificationRule;

@Mapper(componentModel = "spring")
public interface ChannelNotificationDtoMapper {

    @Mapping(target = "email", source = "notificationRule.email")
    @Mapping(target = "notificationId", source = "notificationDto.notificationId")
    @Mapping(target = "createdAt", source = "notificationDto.createdAt")
    @Mapping(target = "sender", source = "notificationDto.sender")
    @Mapping(target = "message", source = "notificationDto.message")
    EmailNotificationDto toEmailNotificationDto(NotificationDto notificationDto, NotificationRule notificationRule);

    @Mapping(target = "phone", source = "notificationRule.phone")
    @Mapping(target = "notificationId", source = "notificationDto.notificationId")
    @Mapping(target = "createdAt", source = "notificationDto.createdAt")
    @Mapping(target = "sender", source = "notificationDto.sender")
    @Mapping(target = "message", source = "notificationDto.message")
    SmsNotificationDto toSmsNotificationDto(NotificationDto notificationDto, NotificationRule notificationRule);

    @Mapping(target = "deviceToken", source = "notificationRule.deviceToken")
    @Mapping(target = "notificationId", source = "notificationDto.notificationId")
    @Mapping(target = "createdAt", source = "notificationDto.createdAt")
    @Mapping(target = "sender", source = "notificationDto.sender")
    @Mapping(target = "message", source = "notificationDto.message")
    PushNotificationDto toPushNotificationDto(NotificationDto notificationDto, NotificationRule notificationRule);

    @Mapping(target = "deviceToken", source = "notificationRule.deviceToken")
    @Mapping(target = "notificationId", source = "notificationDto.notificationId")
    @Mapping(target = "createdAt", source = "notificationDto.createdAt")
    @Mapping(target = "sender", source = "notificationDto.sender")
    @Mapping(target = "message", source = "notificationDto.message")
    WebNotificationDto toWebNotificationDto(NotificationDto notificationDto, NotificationRule notificationRule);

}

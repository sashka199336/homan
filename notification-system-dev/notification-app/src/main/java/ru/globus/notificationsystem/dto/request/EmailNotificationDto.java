package ru.globus.notificationsystem.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailNotificationDto extends ChannelNotificationDto {

    private String email;
}

package ru.globus.notificationsystem.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WebNotificationDto extends ChannelNotificationDto {

    private String deviceToken;
    private String sessionId;
}

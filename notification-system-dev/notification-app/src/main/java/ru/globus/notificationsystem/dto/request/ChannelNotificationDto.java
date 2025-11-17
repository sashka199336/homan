package ru.globus.notificationsystem.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class ChannelNotificationDto {

    private String notificationId;
    private LocalDateTime createdAt;
    private SenderDto sender;
    private String message;
}

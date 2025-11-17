package ru.globus.notificationsystem.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SmsNotificationDto extends ChannelNotificationDto {

    private String phone;
}

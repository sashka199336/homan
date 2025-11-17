package ru.globus.smsnotificationms.dto.response;

import lombok.Data;

@Data
public class NotificationResponse {

    private String id;

    private String notificationId;

    private String channel;

    private String status;

    private String errorCode;

    private String errorMessage;
}


package ru.globus.smsnotificationms.exception;

import lombok.Getter;

@Getter
public class NotificationException extends RuntimeException {

    public NotificationException(String message) {
        super(message);
    }
}

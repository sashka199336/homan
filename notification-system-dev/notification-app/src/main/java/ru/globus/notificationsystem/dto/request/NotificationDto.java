package ru.globus.notificationsystem.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDto {

    private String notificationId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    private SenderDto sender;
    @NotBlank(message = "Client ID is required")
    private String clientId;
    @NotBlank(message = "Message is required")
    private String message;
}

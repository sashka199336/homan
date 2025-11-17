package ru.globus.emailnotificationms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EmailNotificationDto {

    @NotBlank(message = "NotificationId is required")
    private String notificationId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @NotBlank(message = "Sender is required")
    private SenderDto sender;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Email is required")
    private String email;
}

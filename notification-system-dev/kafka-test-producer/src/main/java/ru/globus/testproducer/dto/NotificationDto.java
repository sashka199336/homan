package ru.globus.testproducer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Notification data transfer object")
public class NotificationDto {

    @Schema(description = "Unique identifier of the notification", example = "123e4567-e89b-12d3-a456-426614174000")
    private String notificationId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Schema(description = "Timestamp when notification was created", example = "2024-01-01T12:00:00.000")
    private LocalDateTime createdAt;

    @Schema(description = "Sender information")
    private SenderDto sender;

    @Schema(description = "Client identifier", example = "client-123")
    private String clientId;

    @Schema(description = "Notification message content", example = "Loan application 123 has been approved")
    private String message;
}

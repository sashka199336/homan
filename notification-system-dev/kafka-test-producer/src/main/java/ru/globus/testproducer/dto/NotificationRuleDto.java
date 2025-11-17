package ru.globus.testproducer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Set;

@Data
@Schema(description = "Notification rule data transfer object")
public class NotificationRuleDto {

    @Schema(description = "Client identifier", example = "client-123")
    private String clientId;

    @Schema(description = "Client email address", example = "client@example.com")
    private String email;

    @Schema(description = "Client phone number", example = "+79991234567")
    private String phone;

    @Schema(description = "Preferred notification channels", example = "[\"EMAIL\", \"SMS\"]")
    private Set<Channel> preferNotificationChannels;
}

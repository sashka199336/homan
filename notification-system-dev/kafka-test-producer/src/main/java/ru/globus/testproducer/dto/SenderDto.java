package ru.globus.testproducer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Sender information containing system and user details")
public class SenderDto {

    @Schema(
            description = "System that initiated the notification",
            example = "loan-service",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String system;

    @Schema(
            description = "User identifier within the system who initiated the notification",
            example = "user-123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String userId;
}

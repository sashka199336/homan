package ru.globus.testproducer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Available notification channels",
        enumAsRef = true
)
public enum Channel {

    @Schema(description = "Web notification (in-app or dashboard)")
    WEB,

    @Schema(description = "Email notification")
    EMAIL,

    @Schema(description = "SMS text message notification")
    SMS,

    @Schema(description = "Mobile push notification")
    PUSH
}

package ru.globus.testproducer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.globus.testproducer.dto.NotificationDto;
import ru.globus.testproducer.service.NotificationService;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "API for sending notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Send notification",
            description = "Sends a notification to Kafka topic",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notification sent successfully",
                            content = @Content(schema = @Schema(implementation = String.class)))
                            })
    @PostMapping()
    public String sendNotification(@RequestBody NotificationDto notificationDto) {
        log.info("Sending notification: notificationId {}, clientId {}",
                notificationDto.getNotificationId(), notificationDto.getClientId());
        notificationService.sendNotification(notificationDto);
        return "Notification sent";
    }

    @Operation(
            summary = "Send generated notification",
            description = "Generates and sends a test notification to Kafka topic",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Generated notification sent successfully",
                            content = @Content(schema = @Schema(implementation = String.class)))
            })
    @GetMapping()
    public String sendNotificationGenerated() {
        log.info("Sending generated notification");
        notificationService.sendGeneratedNotification();
        return "Notification sent";
    }
}

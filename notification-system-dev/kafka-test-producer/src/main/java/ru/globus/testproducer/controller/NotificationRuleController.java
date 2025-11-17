package ru.globus.testproducer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.globus.testproducer.dto.NotificationRuleDto;
import ru.globus.testproducer.service.NotificationRuleService;

@Slf4j
@RestController
@RequestMapping("/api/v1/notification-rule")
@RequiredArgsConstructor
@Tag(name = "Notification Rule Controller", description = "API for sending notification rules")
public class NotificationRuleController {

    private final NotificationRuleService notificationRuleService;

    @Operation(
            summary = "Send notification rule",
            description = "Sends a notification rule to Kafka topic",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notification rule sent successfully",
                            content = @Content(schema = @Schema(implementation = String.class)))
            })
    @PostMapping()
    public String sendNotificationRule(@RequestBody NotificationRuleDto notificationRuleDto) {
        log.info("Sending notificationRule: clientId {}", notificationRuleDto.getClientId());
        notificationRuleService.sendNotificationRule(notificationRuleDto);
        return "Notification rule sent";
    }

    @Operation(
            summary = "Send generated notification rule",
            description = "Generates and sends a test notification rule to Kafka topic",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Generated notification rule sent successfully",
                            content = @Content(schema = @Schema(implementation = String.class)))
            })
    @GetMapping()
    public String sendNotificationGenerated() {
        log.info("Sending generated notificationRule");
        notificationRuleService.sendGeneratedNotificationRule();
        return "Notification rule sent";
    }
}

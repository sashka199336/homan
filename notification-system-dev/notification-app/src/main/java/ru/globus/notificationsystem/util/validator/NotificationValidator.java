package ru.globus.notificationsystem.util.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.globus.notificationsystem.annotation.ValidChannelRequirements;

@Component
@Validated
@RequiredArgsConstructor
public class NotificationValidator {

    public void validateChannelRequirements(@ValidChannelRequirements ChannelValidationRequest request) {
    }
}

package ru.globus.notificationsystem.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.globus.notificationsystem.annotation.ValidChannelRequirements;
import ru.globus.notificationsystem.entity.Channel;
import ru.globus.notificationsystem.entity.NotificationRule;

public class ChannelRequirementsValidator implements ConstraintValidator<ValidChannelRequirements, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof ChannelValidationRequest request) {
            return validateChannel(request.channel(), request.rule(), context);
        }
        return false;
    }

    private boolean validateChannel(Channel channel, NotificationRule rule, ConstraintValidatorContext context) {
        switch (channel) {
            case EMAIL -> {
                if (rule.getEmail() == null || rule.getEmail().isBlank()) {
                    addConstraintViolation(context, "Email address is required", "email");
                    return false;
                }
            }
            case SMS -> {
                if (rule.getPhone() == null || rule.getPhone().isBlank()) {
                    addConstraintViolation(context, "Phone number is required", "phone");
                    return false;
                }
            }
            case PUSH -> {
                if (rule.getDeviceToken() == null || rule.getDeviceToken().isBlank()) {
                    addConstraintViolation(context, "Device token is required", "deviceToken");
                    return false;
                }
            }
        }
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message, String property) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(property)
                .addConstraintViolation();
    }
}

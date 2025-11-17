package ru.globus.notificationsystem.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.globus.notificationsystem.util.validator.ChannelRequirementsValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChannelRequirementsValidator.class)
public @interface ValidChannelRequirements {

    String message() default "Invalid channel requirements";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

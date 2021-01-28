package be.fooda.backend.user.service.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TwilioSidValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TwilioSid {
    String message() default "Invalid twilio sid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package be.fooda.backend.user.service.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TwilioSidValidator implements ConstraintValidator<TwilioSid, String> {
    @Override
    public boolean isValid(String contactField, ConstraintValidatorContext cxt) {
        //Twilio sid is like VAXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        return !contactField.isEmpty()
                && contactField.startsWith("VA")
                && contactField.substring(2).matches("^[a-zA-Z0-9]*$")
                && (contactField.length() == 34);
    }
}

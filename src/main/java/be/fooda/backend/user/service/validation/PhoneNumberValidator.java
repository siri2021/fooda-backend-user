package be.fooda.backend.user.service.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// TODO causing problems when there is +32 ..

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String contactField, ConstraintValidatorContext cxt) {
        //Twilio accepts phone number like 32488490509
        return !contactField.isEmpty()
                && contactField.substring(1).matches("^[0-9]*$")
                && (contactField.length() > 10)
                && (contactField.length() < 14);
    }
}

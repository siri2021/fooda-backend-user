package be.fooda.backend.user.model.twilio.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpFailureMessages {

    UNEXPECTED_ERROR_OCCURRED("Unexpected error occurred while the message is being sent."),
    PHONE_NUMBER_FORMAT_IS_NOT_VALID("One of the phone numbers entered is not valid.");

    private String value;
}

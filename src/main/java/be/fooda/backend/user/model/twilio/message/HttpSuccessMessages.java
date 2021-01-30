package be.fooda.backend.user.model.twilio.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpSuccessMessages {

    MESSAGE_IS_SENT("Message is successfully sent to phone number(s).");

    private String value;
}

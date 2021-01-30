package be.fooda.backend.user.model.twilio.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageStatus {

    QUEUED("queued"),
    SENDING("sending"),
    SENT("sent"),
    FAILED("failed"),
    DELIVERED("delivered"),
    UNDELIVERED("undelivered"),
    RECEIVING("receiving"),
    RECEIVED("received"),
    ACCEPTED("accepted"),
    SCHEDULED("scheduled"),
    READ("read"),
    PARTIALLY_DELIVERED("partially_delivered");

    private final String value;

    public String toString() {
        return value;
    }
}

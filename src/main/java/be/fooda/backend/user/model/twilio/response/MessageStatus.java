package be.fooda.backend.user.model.twilio.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.twilio.converter.Promoter;
import com.twilio.rest.api.v2010.account.Message;
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

    /**
     * Generate a Status from a string.
     *
     * @param value string value
     * @return generated Status
     */
    @JsonCreator
    public static Message.Status forValue(final String value) {
        return Promoter.enumFromString(value, Message.Status.values());
    }
}

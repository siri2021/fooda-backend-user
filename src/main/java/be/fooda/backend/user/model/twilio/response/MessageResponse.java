package be.fooda.backend.user.model.twilio.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageResponse {

    private String body;
    private String to;
    private String uri;
    private String accountSid;
    private String messagingServiceSid;
    private MessageStatus status;
    private ZonedDateTime dateSent;
    private ZonedDateTime dateCreated;

}

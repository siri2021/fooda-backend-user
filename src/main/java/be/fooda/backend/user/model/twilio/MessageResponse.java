package be.fooda.backend.user.model.twilio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageResponse {

    private String errorMessage;
    private String dateUpdated;
    private LocalDate dateSent;
    private String dateCreated;
    private String body;
    private String apiVersion;
    private String numSegments;
    private String uri;
    private String sid;
    private String numMedia;
    private BigDecimal priceUnit;
    private String messagingServiceSid;
    private BigDecimal price;
    private String accountSid;
    private String from;
    private Object errorCode;
    private String to;
    private String status;
    private String direction;
}


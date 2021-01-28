package be.fooda.backend.user.model.twilio.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodaTwilioUserRes {
    private String sid;
    private String phone;
    private Boolean isValidated;
}

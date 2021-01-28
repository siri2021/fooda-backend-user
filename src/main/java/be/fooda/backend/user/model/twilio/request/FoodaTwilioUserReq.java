package be.fooda.backend.user.model.twilio.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodaTwilioUserReq {
    private String sid;
    private String phone;
    private String code;
}

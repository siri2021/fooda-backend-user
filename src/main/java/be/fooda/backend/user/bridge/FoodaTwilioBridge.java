package be.fooda.backend.user.bridge;

import be.fooda.backend.user.model.FoodaRole;
import be.fooda.backend.user.model.entity.FoodaUser;
import be.fooda.backend.user.model.twilio.request.FoodaTwilioUserReq;
import be.fooda.backend.user.model.twilio.response.FoodaTwilioUserRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class FoodaTwilioBridge {

    private final RestTemplate restTemplate;

    @Value("${twilio.bridge.url}")
    private String baseURL;

    public Optional<FoodaUser> sendUserSmsCode(String phone, FoodaRole... roles) {

        ResponseEntity<FoodaTwilioUserRes> twilioResponse = restTemplate.getForEntity(baseURL + "sendUserSmsCode?login={phone}", FoodaTwilioUserRes.class, phone);

        if (!twilioResponse.getStatusCode().is2xxSuccessful()) {
            return Optional.empty();
        }

        FoodaUser user = new FoodaUser();
        Set<FoodaRole> roleSet = new HashSet<>(Arrays.asList(roles));
        FoodaTwilioUserRes twilioResponseBody = twilioResponse.getBody();
        user.setLogin(twilioResponseBody.getPhone());
        user.setPassword(twilioResponseBody.getSid());
        user.setRoles(roleSet);

        return Optional.of(user);
    }

    public Optional<FoodaUser> validateUserSmsCode(FoodaUser user, String code) {

        FoodaTwilioUserReq userReq = new FoodaTwilioUserReq();
        userReq.setCode(code);
        userReq.setSid(user.getPassword());
        userReq.setPhone(user.getLogin());

        final ResponseEntity<FoodaTwilioUserRes> twilioResponse =
                restTemplate.postForEntity(baseURL + "validateUserSmsCode", userReq, FoodaTwilioUserRes.class);

        if (!twilioResponse.getStatusCode().is2xxSuccessful()) {
            return Optional.empty();
        }

        if (twilioResponse.hasBody() && twilioResponse.getBody().getIsValidated() != null)
            user.setIsAuthenticated(twilioResponse.getBody().getIsValidated());

        return Optional.of(user);
    }
}
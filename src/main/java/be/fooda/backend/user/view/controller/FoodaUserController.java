package be.fooda.backend.user.view.controller;

import be.fooda.backend.user.bridge.FoodaTwilioBridge;
import be.fooda.backend.user.dao.FoodaUserRepository;
import be.fooda.backend.user.model.entity.FoodaUser;
import be.fooda.backend.user.model.http.FoodaUserHttpFailureMessages;
import be.fooda.backend.user.model.http.FoodaUserHttpSuccessMessages;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/")
public class FoodaUserController {

    private final FoodaTwilioBridge twilioBridge;
    private final FoodaUserRepository userRepository;

    @ApiOperation(
            value = "Send SMS verification code to user. It is will generate a code with 6 digits.",
            notes = "If the user is new, it creates a user in DB and then sets a validation code. " +
                    "If the user already exists it just generates a validation code. " +
                    "It will connect to Twilio SMS API and send a message using related credentials."
    )
    @GetMapping("code")
    public ResponseEntity sendCode(@RequestParam String phone) {

        int min = 100_000;
        int max = 999_999;
        String code = String.valueOf(new Random().nextInt(max) + min);

        boolean isCodeSent = twilioBridge.sendCode(phone, code);

        if (!isCodeSent) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.SMS_CODE_COULD_NOT_BE_SENT_TO_USER);
        }

        final Optional<FoodaUser> existingUser = userRepository.findByLoginAndIsActive(phone, true);

        if (userRepository.existsByLoginAndIsActive(phone, false)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.USER_IS_DELETED_CANNOT_LOGIN);
        }

        if (existingUser.isPresent()) {
            final FoodaUser existingUserBeingUpdated = existingUser.get();
            existingUserBeingUpdated.setIsAuthenticated(false);
            existingUserBeingUpdated.setValidationExpiry(LocalDateTime.now().plusHours(2));
            existingUserBeingUpdated.setValidationCode(code);
            userRepository.save(existingUserBeingUpdated);
        } else {
            FoodaUser newUserBeingCreated = new FoodaUser();
            newUserBeingCreated.setLogin(phone);
            newUserBeingCreated.setValidationCode(code);
            userRepository.save(newUserBeingCreated);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(FoodaUserHttpSuccessMessages.SMS_CODE_IS_SENT);
    }

    @ApiOperation(
            value = "Send SMS notification to the user . It is will generate a code with 6 digits.",
            notes = "It will connect to Twilio SMS API and send a message using related credentials."
    )
    @GetMapping("validate")
    public ResponseEntity validateCode(@RequestParam String phone, @RequestParam String code) {

        if (userRepository.existsByLoginAndIsActive(phone, false))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.USER_IS_DELETED_CANNOT_BE_VALIDATED);

        final Optional<FoodaUser> foundUserByLogin = userRepository.findByLoginAndIsActive(phone, true);

        if (!foundUserByLogin.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);

        if (foundUserByLogin.get().getIsAuthenticated().equals(Boolean.TRUE))
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(FoodaUserHttpSuccessMessages.USER_CODE_IS_VALID);

        final FoodaUser userBeingAuthenticated = foundUserByLogin.get();

        if (!userBeingAuthenticated.getValidationCode().equalsIgnoreCase(code))
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(FoodaUserHttpSuccessMessages.USER_CODE_IS_NOT_VALID);

        userBeingAuthenticated.setIsAuthenticated(Boolean.TRUE);
        userBeingAuthenticated.setValidationExpiry(LocalDateTime.now().plusHours(2));
        userRepository.save(userBeingAuthenticated);

        twilioBridge.sendValidated(phone);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(FoodaUserHttpSuccessMessages.USER_CODE_IS_VALID);
    }

    @GetMapping("exists")
    public ResponseEntity existsById(@RequestParam UUID id) {

        final boolean userExists = userRepository.existsById(id);

        if (!userExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(FoodaUserHttpFailureMessages.USER_EXISTS);
    }

    @GetMapping("get_by_id")
    public ResponseEntity getById(@RequestParam UUID id) {

        final Optional<FoodaUser> foundUser = userRepository.findById(id);

        if (!foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(foundUser);
    }

    @GetMapping("get_by_phone")
    public ResponseEntity getByPhone(@RequestParam String phone) {

        final Optional<FoodaUser> foundUser = userRepository.findByLoginAndIsActive(phone, true);

        if (!foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(foundUser);
    }

    @DeleteMapping("delete_by_phone")
    public ResponseEntity deleteById(@RequestParam String phone) {

        Optional<FoodaUser> foundUser = userRepository.findByLoginAndIsActive(phone, true);

        if (!foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        if (foundUser.get().getIsActive().equals(Boolean.FALSE)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.USER_ALREADY_DELETED);
        }

        final FoodaUser userBeingDeleted = foundUser.get();
        userBeingDeleted.setIsActive(Boolean.FALSE);
        userRepository.save(userBeingDeleted);

        if (!userRepository.existsByLoginAndIsActive(phone, true)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(FoodaUserHttpFailureMessages.USER_COULD_NOT_BE_DELETED);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(FoodaUserHttpSuccessMessages.USER_DELETED);
    }

}

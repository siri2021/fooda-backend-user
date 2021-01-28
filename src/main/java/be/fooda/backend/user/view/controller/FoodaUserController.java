package be.fooda.backend.user.view.controller;

import be.fooda.backend.user.bridge.FoodaTwilioBridge;
import be.fooda.backend.user.dao.FoodaUserRepository;
import be.fooda.backend.user.model.entity.FoodaUser;
import be.fooda.backend.user.model.http.FoodaUserHttpFailureMessages;
import be.fooda.backend.user.model.http.FoodaUserHttpSuccessMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/")
public class FoodaUserController {

    private final FoodaTwilioBridge twilioBridge;
    private final FoodaUserRepository userRepository;

    @GetMapping("send_sms_code")
    public ResponseEntity sendSmsCode(@RequestParam final String phoneNumber) {

        final Optional<FoodaUser> oUser = twilioBridge.sendUserSmsCode(phoneNumber);

        if (!oUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.SMS_CODE_COULD_NOT_BE_SENT_TO_USER);
        }

        final Optional<FoodaUser> existingUser = userRepository.findByLoginAndIsActive(phoneNumber, true);

        if (userRepository.existsByLoginAndIsActive(phoneNumber, false)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.USER_IS_DELETED_CANNOT_LOGIN);
        }

        if (existingUser.isPresent()) {
            final FoodaUser existingUserBeingUpdated = existingUser.get();
            existingUserBeingUpdated.setIsAuthenticated(false);
            userRepository.save(existingUserBeingUpdated);
        } else {
            userRepository.save(oUser.get());
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(FoodaUserHttpSuccessMessages.SMS_CODE_IS_SENT);
    }

    @GetMapping("validate_sms_code")
    public ResponseEntity validateSmsCode(@RequestParam final String phoneNumber, @RequestParam final String smsCode) {

        if (userRepository.existsByLoginAndIsActive(phoneNumber, false)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.USER_IS_DELETED_CANNOT_BE_VALIDATED);
        }

        final Optional<FoodaUser> foundUserByLogin = userRepository.findByLoginAndIsActive(phoneNumber, true);

        if (!foundUserByLogin.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        // TODO there is still logical incomplete implementation here ..
        // we are checking if user is authenticated before sending validate by sms request to twilio..
        // but what if someone updated the database isAuthenticated column to true via SQL query..
        // this is a leak in the flow.. but this code is good enough for dev environment
        if (foundUserByLogin.get().getIsAuthenticated().equals(Boolean.TRUE)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(FoodaUserHttpSuccessMessages.USER_IS_VALID);
        }

        final FoodaUser userBeingAuthenticated = foundUserByLogin.get();
        final Optional<FoodaUser> oUser = twilioBridge.validateUserSmsCode(userBeingAuthenticated, smsCode);

        if (!oUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.SMS_CODE_COULD_NOT_BE_SENT_TO_USER);
        }

        userRepository.save(userBeingAuthenticated);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(FoodaUserHttpSuccessMessages.USER_IS_VALID);
    }

    @GetMapping("validate_sms_code_for_update")
    public ResponseEntity validateSmsCodeForUpdate(
            @RequestParam final String existingPhoneNumber,
            @RequestParam final String newPhoneNumber,
            @RequestParam final String smsCodeFromExistingPhone,
            @RequestParam final String smsCodeFromNewPhone) {

        // the existing phone number must be in the DB ..
        final Optional<FoodaUser> foundExistingUser = userRepository.findByLoginAndIsActive(existingPhoneNumber, true);

        // the code from existing phone must valid ..
        if (!foundExistingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }
        final Optional<FoodaUser> oExistingUser = twilioBridge.validateUserSmsCode(foundExistingUser.get(), smsCodeFromExistingPhone);

        if (!oExistingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.SMS_CODE_COULD_NOT_BE_SENT_TO_USER);
        }

        // the new phone number must not exist in the DB ..
        boolean doesNewPhoneNumberExist = userRepository.existsByLogin(newPhoneNumber);

        if (doesNewPhoneNumberExist) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.USER_TO_UPDATE_ALREADY_EXIST);
        }

        // the code from new phone number must also be valid ..
        final Optional<FoodaUser> oNewUser = twilioBridge.validateUserSmsCode(foundExistingUser.get(), smsCodeFromNewPhone);

        if (!oNewUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.SMS_CODE_COULD_NOT_BE_SENT_TO_USER);
        }

        final FoodaUser fromExistingUserToNewUser = oExistingUser.get();
        fromExistingUserToNewUser.setLogin(newPhoneNumber);
        fromExistingUserToNewUser.setIsAuthenticated(true);
        userRepository.save(fromExistingUserToNewUser);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(FoodaUserHttpSuccessMessages.USER_UPDATED);
    }

    @GetMapping("exist_by_user_id")
    public ResponseEntity existByUserId(@RequestParam Long id) {

        final boolean userExists = userRepository.existsById(id);

        if (!userExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(FoodaUserHttpFailureMessages.USER_EXISTS);
    }

    @GetMapping("get_user_by_id")
    public ResponseEntity getUserById(@RequestParam Long id) {

        final Optional<FoodaUser> foundUser = userRepository.findById(id);

        if (!foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(foundUser);
    }

    @GetMapping("get_user_by_username")
    public ResponseEntity getUserByUsername(@RequestParam String username) {

        final Optional<FoodaUser> foundUser = userRepository.findByLoginAndIsActive(username, true);

        if (!foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(foundUser);
    }

    @PatchMapping("delete_user_by_username")
    public ResponseEntity deleteUserById(@RequestParam String username) {

        Optional<FoodaUser> foundUser = userRepository.findByLoginAndIsActive(username, true);

        if (!foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        if (foundUser.get().getIsActive().equals(Boolean.FALSE)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(FoodaUserHttpFailureMessages.USER_ALREADY_DELETED);
        }

        final FoodaUser userBeingDeleted = foundUser.get();
        userBeingDeleted.setIsActive(Boolean.FALSE);
        userRepository.save(userBeingDeleted);

        if (!userRepository.existsByLoginAndIsActive(username, true)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(FoodaUserHttpFailureMessages.USER_COULD_NOT_BE_DELETED);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(FoodaUserHttpSuccessMessages.USER_DELETED);
    }

}

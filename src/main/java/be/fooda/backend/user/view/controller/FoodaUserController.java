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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/")
public class FoodaUserController {

    private final FoodaTwilioBridge twilioBridge;
    private final FoodaUserRepository userRepository;

    @GetMapping("code")
    public ResponseEntity sendCode(@RequestParam final String phone) {

        int min = 100_000;
        int max = 999_999;
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(min, max) + min);

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

    @GetMapping("validate")
    public ResponseEntity validateCode(@RequestParam final String phone, @RequestParam final String code) {

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

    @GetMapping("exists/{id}")
    public ResponseEntity existsById(@PathVariable Long id) {

        final boolean userExists = userRepository.existsById(id);

        if (!userExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(FoodaUserHttpFailureMessages.USER_EXISTS);
    }

    @GetMapping("{id}")
    public ResponseEntity getById(@PathVariable Long id) {

        final Optional<FoodaUser> foundUser = userRepository.findById(id);

        if (!foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FoodaUserHttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(foundUser);
    }

    @GetMapping("phone/{phone}")
    public ResponseEntity getByPhone(@RequestParam String phone) {

        final Optional<FoodaUser> foundUser = userRepository.findByLoginAndIsActive(phone, true);

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

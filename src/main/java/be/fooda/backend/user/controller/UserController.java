package be.fooda.backend.user.controller;

import be.fooda.backend.user.bridge.TwilioBridge;
import be.fooda.backend.user.dao.UserRepository;
import be.fooda.backend.user.model.create.RoleCreate;
import be.fooda.backend.user.model.entity.RoleEntity;
import be.fooda.backend.user.model.entity.UserEntity;
import be.fooda.backend.user.model.http.HttpFailureMessages;
import be.fooda.backend.user.model.http.HttpSuccessMessages;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/")
public class UserController {

    private final TwilioBridge twilioBridge;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @ApiOperation(
            value = "Send SMS verification code to user. It is will generate a code with 6 digits.",
            notes = "If the user is new, it creates a user in DB and then sets a validation code. " +
                    "If the user already exists it just generates a validation code. " +
                    "It will connect to Twilio SMS API and send a message using related credentials."
    )
    @Transactional
    @GetMapping("code")
    public ResponseEntity sendCode(@RequestParam String phone) {

        int min = 100_000;
        int max = 999_999;
        String code = String.valueOf(new Random().nextInt(max) + min);

        String password = generatePassword(12);
        twilioBridge.sendCode(phone, code, password);

        if (userRepository.existsByLogin(phone)) {

            final UserEntity existingUserBeingUpdated = userRepository.getOneByLogin(phone);

            if (existingUserBeingUpdated.getIsActive().equals(Boolean.FALSE))
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HttpFailureMessages.USER_IS_DELETED_CANNOT_LOGIN);

            existingUserBeingUpdated.setIsAuthenticated(false);
            existingUserBeingUpdated.setValidationExpiry(LocalDateTime.now().plusHours(2));
            existingUserBeingUpdated.setValidationCode(code);
            existingUserBeingUpdated.setPassword(passwordEncoder.encode(password));
            userRepository.save(existingUserBeingUpdated);

        } else {

            UserEntity newUserBeingCreated = new UserEntity();
            newUserBeingCreated.setLogin(phone);
            newUserBeingCreated.setValidationCode(code);
            newUserBeingCreated.setPassword(password);
            newUserBeingCreated.setPassword(passwordEncoder.encode(password));
            userRepository.save(newUserBeingCreated);
        }

        log.trace("Validation code is sent to: " + phone);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpSuccessMessages.SMS_CODE_IS_SENT);
    }

    @ApiOperation(
            value = "Send SMS notification to the user . It is will generate a code with 6 digits.",
            notes = "It will connect to Twilio SMS API and send a message using related credentials."
    )
    @Transactional
    @GetMapping("validate")
    public ResponseEntity validateCode(@RequestParam String phone, @RequestParam String code) {

        if (!userRepository.existsByLogin(phone))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HttpFailureMessages.USER_DOES_NOT_EXIST);

        UserEntity foundUserByLogin = userRepository.getOneByLogin(phone);

        if (foundUserByLogin.getIsActive().equals(Boolean.FALSE))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HttpFailureMessages.USER_IS_DELETED_CANNOT_BE_VALIDATED);

        if (foundUserByLogin.getIsAuthenticated().equals(Boolean.TRUE))
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpSuccessMessages.USER_CODE_IS_VALID);

        if (foundUserByLogin.getValidationExpiry().isBefore(LocalDateTime.now()))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HttpFailureMessages.VALIDATION_CODE_IS_EXPIRED);

        if (!foundUserByLogin.getValidationCode().equalsIgnoreCase(code))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpFailureMessages.USER_CODE_IS_NOT_VALID);

        foundUserByLogin.setIsAuthenticated(Boolean.TRUE);
        userRepository.save(foundUserByLogin);

        twilioBridge.sendValidated(phone);

        log.trace("Validation for " + phone + " is valid.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpSuccessMessages.USER_CODE_IS_VALID);
    }

    @PutMapping("add_role")
    public ResponseEntity addRoleToUser(@RequestParam String phone, @RequestParam RoleCreate role) {

        if (!userRepository.existsByLogin(phone))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HttpFailureMessages.USER_DOES_NOT_EXIST);

        UserEntity foundUserByLogin = userRepository.getOneByLogin(phone);

        if (foundUserByLogin.getIsActive().equals(Boolean.FALSE))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HttpFailureMessages.USER_IS_DELETED_CANNOT_BE_VALIDATED);

        RoleEntity roleEntity = RoleEntity.valueOf(role.name());
        foundUserByLogin.getRoles().add(roleEntity);
        userRepository.save(foundUserByLogin);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpSuccessMessages.ROLE_IS_ADDED);
    }

    @GetMapping("exists")
    public ResponseEntity existsById(@RequestParam UUID id) {

        final boolean userExists = userRepository.existsById(id);

        if (!userExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(HttpFailureMessages.USER_EXISTS);
    }

    @GetMapping("get_by_id")
    public ResponseEntity getById(@RequestParam UUID id) {

        final Optional<UserEntity> foundUser = userRepository.findById(id);

        if (!foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        return ResponseEntity.status(HttpStatus.FOUND).body(foundUser);
    }

    @GetMapping("get_by_phone")
    public ResponseEntity getByPhone(@RequestParam String phone) {

        final Optional<UserEntity> foundUserByLogin = userRepository.findByLogin(phone);

        if (!foundUserByLogin.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        if (foundUserByLogin.get().getIsActive().equals(Boolean.FALSE))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HttpFailureMessages.USER_IS_DELETED);

        return ResponseEntity.status(HttpStatus.FOUND).body(foundUserByLogin);
    }

    @Transactional
    @DeleteMapping("delete_by_phone")
    public ResponseEntity deleteById(@RequestParam String phone) {

        Optional<UserEntity> foundUserByLogin = userRepository.findByLogin(phone);

        if (!foundUserByLogin.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpFailureMessages.USER_DOES_NOT_EXIST);
        }

        if (foundUserByLogin.get().getIsActive().equals(Boolean.FALSE)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(HttpFailureMessages.USER_ALREADY_DELETED);
        }

        foundUserByLogin.get().setIsActive(Boolean.FALSE);
        userRepository.save(foundUserByLogin.get());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpSuccessMessages.USER_DELETED);
    }


    //    PRIVATE METHODS FOR CONTROLLER ..
    private String generatePassword(int length) {
        return new RandomStringGenerator.Builder()
                .withinRange(53, 76)
                .build().generate(length);
    }

}

package com.cpp.project.authentication.service;

import com.cpp.project.common.sanitization.adapter.LoginRequestSanitizer;
import com.cpp.project.common.sanitization.adapter.SignUpRequestSanitizer;
import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.UserValidationService;
import com.cpp.project.user.adapter.UserAdapter;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user.entity.*;
import com.cpp.project.user.service.UserService;
import com.cpp.project.user_credential.entity.UserCredentialException;
import com.cpp.project.user_credential.service.UserCredentialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final UserService userService;
    private final UserCredentialService credentialService;
    private final UserValidationService validationService;
    private final SignUpRequestSanitizer signUpSanitizer;
    private final LoginRequestSanitizer loginSanitizer;

    public AuthenticationServiceImpl(UserService userService,
                                     UserCredentialService credentialService,
                                     UserValidationService validationService,
                                     SignUpRequestSanitizer signUpSanitizer,
                                     LoginRequestSanitizer loginSanitizer) {
        this.userService = userService;
        this.credentialService = credentialService;
        this.validationService = validationService;
        this.signUpSanitizer = signUpSanitizer;
        this.loginSanitizer = loginSanitizer;
    }

    @Override
    public UserDTO signUp(SignUpRequestDTO request) {
        SignUpRequestDTO sanitizedRequest = signUpSanitizer.sanitize(request);

        ValidationResult validationResult = validationService.validateSignUpRequest(sanitizedRequest);

        if (!validationResult.isValid()) {
            throw new UserException(UserErrorCode.INVALID_USER_DATA, validationResult.getFirstError());
        }

        try {
            User user = userService.createUserWithoutCredential(sanitizedRequest.getName(), sanitizedRequest.getEmail());

            credentialService.createCredential(user.getId(), sanitizedRequest.getPassword());

            logger.info("User signed up successfully: {}", user.getEmail());
            return UserAdapter.toDTO(user);
        } catch (UserException | UserCredentialException e) {
            logger.error("Failed during sign up for email: {}", sanitizedRequest.getEmail(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during sign up for email: {}", sanitizedRequest.getEmail(), e);
            throw new UserException(UserErrorCode.USER_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean login(LoginRequestDTO request) {
        LoginRequestDTO sanitizedRequest = loginSanitizer.sanitize(request);

        ValidationResult validationResult = validationService.validateLoginRequest(sanitizedRequest);

        if (!validationResult.isValid()) {
            logger.warn("Login validation failed for email: {}", sanitizedRequest.getEmail());
            throw new AuthenticationException(AuthenticationErrorCode.INVALID_CREDENTIALS);
        }

        try {
            UserDTO user = userService.getUserByEmail(sanitizedRequest.getEmail());
            boolean isValid = credentialService.verifyPassword(sanitizedRequest.getEmail(), sanitizedRequest.getPassword());

            if (isValid) {
                logger.info("User logged in successfully: {}", sanitizedRequest.getEmail());
            } else {
                logger.warn("Failed login attempt for user: {}", sanitizedRequest.getEmail());
            }

            return isValid;
        } catch (UserException | UserCredentialException | AuthenticationException e) {
            // Convert domain exceptions to authentication failure (don't reveal user existence)
            logger.warn("Login failed for email: {} - {}", sanitizedRequest.getEmail(), e.getClass().getSimpleName());
            throw new AuthenticationException(AuthenticationErrorCode.INVALID_CREDENTIALS);
        } catch (Exception e) {
            logger.error("Unexpected error during login for email: {}", sanitizedRequest.getEmail(), e);
            throw new AuthenticationException(AuthenticationErrorCode.AUTHENTICATION_FAILED, e, sanitizedRequest.getEmail());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }
}

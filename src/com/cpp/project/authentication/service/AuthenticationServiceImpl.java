package com.cpp.project.authentication.service;

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

    public AuthenticationServiceImpl(UserService userService,
                                     UserCredentialService credentialService,
                                     UserValidationService validationService) {
        this.userService = userService;
        this.credentialService = credentialService;
        this.validationService = validationService;
    }

    @Override
    public UserDTO signUp(SignUpRequestDTO request) {
        try {
            // Validate entire request using validation framework
            ValidationResult validationResult = validationService.validateSignUpRequest(request);

            if (!validationResult.isValid()) {
                String firstError = validationResult.getFirstError();
                throw new UserException(UserErrorCode.INVALID_USER_DATA, firstError);
            }

            // Create user without credential (delegated to UserService)
            User user = userService.createUserWithoutCredential(request.getName(), request.getEmail());

            // Create credential for user (delegated to UserCredentialService)
            credentialService.createCredential(user.getId(), request.getPassword());

            logger.info("User signed up successfully: {}", user.getEmail());
            return UserAdapter.toDTO(user);
        } catch (UserException | UserCredentialException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during sign up", e);
            throw new UserException(UserErrorCode.USER_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean login(LoginRequestDTO request) {
        try {
            // Validate request using validation framework
            ValidationResult validationResult = validationService.validateLoginRequest(request);

            if (!validationResult.isValid()) {
                throw new AuthenticationException(AuthenticationErrorCode.INVALID_CREDENTIALS);
            }

            // Verify user exists (delegated to UserService)
            UserDTO user = userService.getUserByEmail(request.getEmail());
            if (user == null) {
                throw new AuthenticationException(AuthenticationErrorCode.INVALID_CREDENTIALS);
            }

            // Verify password (delegated to UserCredentialService)
            boolean isValid = credentialService.verifyPassword(request.getEmail(), request.getPassword());

            if (isValid) {
                logger.info("User logged in successfully: {}", request.getEmail());
            } else {
                logger.warn("Failed login attempt for user: {}", request.getEmail());
            }

            return isValid;
        } catch (UserException | UserCredentialException | AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            throw new AuthenticationException(AuthenticationErrorCode.AUTHENTICATION_FAILED, e, request.getEmail());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }
}

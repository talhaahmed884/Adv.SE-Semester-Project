package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class UserValidationService {
    private final SignUpRequestValidator signUpRequestValidator = new SignUpRequestValidator();
    private final LoginRequestValidator loginRequestValidator = new LoginRequestValidator();
    private final EmailValidator emailValidator = new EmailValidator();
    private final UserNameValidator userNameValidator = new UserNameValidator();
    private final PasswordInputValidator passwordInputValidator = new PasswordInputValidator();
    private final PasswordStrengthValidator passwordStrengthValidator = new PasswordStrengthValidator();
    private final PasswordHashValidator passwordHashValidator = new PasswordHashValidator();

    public ValidationResult validateSignUpRequest(SignUpRequestDTO request) {
        return signUpRequestValidator.validate(request);
    }

    public ValidationResult validateLoginRequest(LoginRequestDTO request) {
        return loginRequestValidator.validate(request);
    }

    public ValidationResult validateEmail(String email) {
        return emailValidator.validate(email);
    }

    public ValidationResult validateUserName(String name) {
        return userNameValidator.validate(name);
    }

    // For login/verify operations - only checks if password is provided
    public ValidationResult validatePasswordInput(String password) {
        return passwordInputValidator.validate(password);
    }

    // For signup/setPassword operations - checks password strength
    public ValidationResult validatePasswordStrength(String password) {
        return passwordStrengthValidator.validate(password);
    }

    public ValidationResult validatePasswordHash(String passwordHash) {
        return passwordHashValidator.validate(passwordHash);
    }
}

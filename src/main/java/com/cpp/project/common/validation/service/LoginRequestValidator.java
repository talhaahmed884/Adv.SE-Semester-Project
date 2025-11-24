package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;
import com.cpp.project.user.dto.LoginRequestDTO;

public class LoginRequestValidator extends Validator<LoginRequestDTO> {
    private final EmailValidator emailValidator = new EmailValidator();
    private final PasswordInputValidator passwordValidator = new PasswordInputValidator();

    @Override
    protected void performValidation(LoginRequestDTO request, ValidationResultBuilder resultBuilder) {
        if (request == null) {
            resultBuilder.addError("Login request cannot be null");
            return;
        }

        // Validate email
        ValidationResult emailResult = emailValidator.validate(request.getEmail());
        if (!emailResult.isValid()) {
            resultBuilder.addErrors(emailResult.getErrors());
        }

        // Validate password
        ValidationResult passwordResult = passwordValidator.validate(request.getPassword());
        if (!passwordResult.isValid()) {
            resultBuilder.addErrors(passwordResult.getErrors());
        }
    }
}

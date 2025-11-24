package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;
import com.cpp.project.user.dto.SignUpRequestDTO;

public class SignUpRequestValidator extends Validator<SignUpRequestDTO> {
    private final UserNameValidator nameValidator = new UserNameValidator();
    private final EmailValidator emailValidator = new EmailValidator();
    private final PasswordStrengthValidator passwordValidator = new PasswordStrengthValidator();

    @Override
    protected void performValidation(SignUpRequestDTO request, ValidationResultBuilder resultBuilder) {
        if (request == null) {
            resultBuilder.addError("Sign up request cannot be null");
            return;
        }

        // Validate name
        ValidationResult nameResult = nameValidator.validate(request.getName());
        if (!nameResult.isValid()) {
            resultBuilder.addErrors(nameResult.getErrors());
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

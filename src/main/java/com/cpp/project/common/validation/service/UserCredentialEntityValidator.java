package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;
import com.cpp.project.common.validation.rule.NotNullRule;
import com.cpp.project.user_credential.entity.UserCredential;

public class UserCredentialEntityValidator extends Validator<UserCredential> {
    private final PasswordHashValidator passwordHashValidator = new PasswordHashValidator();

    @Override
    protected void performValidation(UserCredential credential, ValidationResultBuilder resultBuilder) {
        NotNullRule<UserCredential> notNullRule = new NotNullRule<>("User credential");
        if (!notNullRule.isValid(credential)) {
            resultBuilder.addError(notNullRule.getErrorMessage());
            return;
        }

        // Validate password hash
        ValidationResult hashResult = passwordHashValidator.validate(credential.getPasswordHash());
        if (!hashResult.isValid()) {
            resultBuilder.addErrors(hashResult.getErrors());
        }

        // Validate algorithm
        if (credential.getAlgorithm() == null || credential.getAlgorithm().trim().isEmpty()) {
            resultBuilder.addError("Algorithm cannot be null or empty");
        }
    }
}

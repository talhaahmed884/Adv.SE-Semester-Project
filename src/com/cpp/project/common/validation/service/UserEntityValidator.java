package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;
import com.cpp.project.common.validation.rule.NotNullRule;
import com.cpp.project.user.entity.User;

public class UserEntityValidator extends Validator<User> {
    private final UserNameValidator nameValidator = new UserNameValidator();
    private final EmailValidator emailValidator = new EmailValidator();

    @Override
    protected void performValidation(User user, ValidationResultBuilder resultBuilder) {
        NotNullRule<User> notNullRule = new NotNullRule<>("User");
        if (!notNullRule.isValid(user)) {
            resultBuilder.addError(notNullRule.getErrorMessage());
            return;
        }

        // Validate name
        ValidationResult nameResult = nameValidator.validate(user.getName());
        if (!nameResult.isValid()) {
            resultBuilder.addErrors(nameResult.getErrors());
        }

        // Validate email
        ValidationResult emailResult = emailValidator.validate(user.getEmail());
        if (!emailResult.isValid()) {
            resultBuilder.addErrors(emailResult.getErrors());
        }
    }
}

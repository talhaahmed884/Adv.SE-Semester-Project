package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;
import com.cpp.project.common.validation.rule.NotEmptyStringRule;
import com.cpp.project.common.validation.rule.PasswordStrengthRule;

public class PasswordStrengthValidator extends Validator<String> {
    private final PasswordStrengthRule strengthRule;

    public PasswordStrengthValidator() {
        this.strengthRule = PasswordStrengthRule.basic();
    }

    public PasswordStrengthValidator(PasswordStrengthRule strengthRule) {
        this.strengthRule = strengthRule;
    }

    @Override
    protected void performValidation(String password, ValidationResultBuilder resultBuilder) {
        NotEmptyStringRule notEmptyRule = new NotEmptyStringRule("Password");
        if (!notEmptyRule.isValid(password)) {
            resultBuilder.addError(notEmptyRule.getErrorMessage());
            return;
        }

        if (!strengthRule.isValid(password)) {
            resultBuilder.addError(strengthRule.getErrorMessage());
        }
    }
}

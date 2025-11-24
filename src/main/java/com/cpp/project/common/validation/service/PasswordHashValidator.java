package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;
import com.cpp.project.common.validation.rule.NotEmptyStringRule;
import com.cpp.project.common.validation.rule.PasswordHashFormatRule;

public class PasswordHashValidator extends Validator<String> {
    @Override
    protected void performValidation(String passwordHash, ValidationResultBuilder resultBuilder) {
        NotEmptyStringRule notEmptyRule = new NotEmptyStringRule("Password hash");
        if (!notEmptyRule.isValid(passwordHash)) {
            resultBuilder.addError(notEmptyRule.getErrorMessage());
            return;
        }

        PasswordHashFormatRule hashFormatRule = new PasswordHashFormatRule();
        if (!hashFormatRule.isValid(passwordHash)) {
            resultBuilder.addError(hashFormatRule.getErrorMessage());
        }
    }
}

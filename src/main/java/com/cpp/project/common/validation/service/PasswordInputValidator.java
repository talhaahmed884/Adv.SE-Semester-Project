package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;
import com.cpp.project.common.validation.rule.NotEmptyStringRule;

public class PasswordInputValidator extends Validator<String> {
    @Override
    protected void performValidation(String password, ValidationResultBuilder resultBuilder) {
        NotEmptyStringRule notEmptyRule = new NotEmptyStringRule("Password");
        if (!notEmptyRule.isValid(password)) {
            resultBuilder.addError(notEmptyRule.getErrorMessage());
        }
    }
}

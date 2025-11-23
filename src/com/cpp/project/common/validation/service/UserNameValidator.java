package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;
import com.cpp.project.common.validation.rule.NotEmptyStringRule;
import com.cpp.project.common.validation.rule.UserNameFormatRule;

public class UserNameValidator extends Validator<String> {
    @Override
    protected void performValidation(String name, ValidationResultBuilder resultBuilder) {
        NotEmptyStringRule notEmptyRule = new NotEmptyStringRule("Name");
        if (!notEmptyRule.isValid(name)) {
            resultBuilder.addError(notEmptyRule.getErrorMessage());
            return;
        }

        UserNameFormatRule nameFormatRule = new UserNameFormatRule();
        if (!nameFormatRule.isValid(name)) {
            resultBuilder.addError(nameFormatRule.getErrorMessage());
        }
    }
}

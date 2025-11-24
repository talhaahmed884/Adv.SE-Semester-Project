package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;
import com.cpp.project.common.validation.rule.EmailFormatRule;
import com.cpp.project.common.validation.rule.NotEmptyStringRule;

public class EmailValidator extends Validator<String> {
    @Override
    protected void performValidation(String email, ValidationResultBuilder resultBuilder) {
        NotEmptyStringRule notEmptyRule = new NotEmptyStringRule("Email");
        if (!notEmptyRule.isValid(email)) {
            resultBuilder.addError(notEmptyRule.getErrorMessage());
            return;
        }

        EmailFormatRule emailFormatRule = new EmailFormatRule();
        if (!emailFormatRule.isValid(email)) {
            resultBuilder.addError(emailFormatRule.getErrorMessage());
        }
    }
}

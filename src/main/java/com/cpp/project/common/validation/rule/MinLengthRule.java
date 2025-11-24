package com.cpp.project.common.validation.rule;

import com.cpp.project.common.validation.entity.ValidationRule;

public class MinLengthRule implements ValidationRule<String> {
    private final String fieldName;
    private final int minLength;

    public MinLengthRule(String fieldName, int minLength) {
        this.fieldName = fieldName;
        this.minLength = minLength;
    }

    @Override
    public boolean isValid(String value) {
        return value != null && value.length() >= minLength;
    }

    @Override
    public String getErrorMessage() {
        return fieldName + " must be at least " + minLength + " characters long";
    }
}

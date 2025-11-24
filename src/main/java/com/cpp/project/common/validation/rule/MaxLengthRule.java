package com.cpp.project.common.validation.rule;

import com.cpp.project.common.validation.entity.ValidationRule;

public class MaxLengthRule implements ValidationRule<String> {
    private final String fieldName;
    private final int maxLength;

    public MaxLengthRule(String fieldName, int maxLength) {
        this.fieldName = fieldName;
        this.maxLength = maxLength;
    }

    @Override
    public boolean isValid(String value) {
        return value == null || value.length() <= maxLength;
    }

    @Override
    public String getErrorMessage() {
        return fieldName + " must not exceed " + maxLength + " characters";
    }
}

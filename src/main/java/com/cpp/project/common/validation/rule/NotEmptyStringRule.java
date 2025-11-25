package com.cpp.project.common.validation.rule;

import com.cpp.project.common.validation.entity.ValidationRule;

public class NotEmptyStringRule implements ValidationRule<String> {
    private final String fieldName;

    public NotEmptyStringRule(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Override
    public String getErrorMessage() {
        return fieldName + " cannot be null or empty";
    }
}

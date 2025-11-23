package com.cpp.project.common.validation.rule;

import com.cpp.project.common.validation.entity.ValidationRule;

public class NotNullRule<T> implements ValidationRule<T> {
    private final String fieldName;

    public NotNullRule(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public boolean isValid(T value) {
        return value != null;
    }

    @Override
    public String getErrorMessage() {
        return fieldName + " cannot be null";
    }
}

package com.cpp.project.common.validation.rule;

import com.cpp.project.common.validation.entity.ValidationRule;

public class EmailFormatRule implements ValidationRule<String> {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Override
    public boolean isValid(String value) {
        return value != null && value.matches(EMAIL_REGEX);
    }

    @Override
    public String getErrorMessage() {
        return "Invalid email format";
    }
}

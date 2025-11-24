package com.cpp.project.common.validation.rule;

import com.cpp.project.common.validation.entity.ValidationRule;

public class UserNameFormatRule implements ValidationRule<String> {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    @Override
    public boolean isValid(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String trimmedName = name.trim();
        if (trimmedName.length() < MIN_LENGTH || trimmedName.length() > MAX_LENGTH) {
            return false;
        }

        // Name should only contain letters, spaces, hyphens, and apostrophes
        return trimmedName.matches("^[a-zA-Z\\s'-]+$");
    }

    @Override
    public String getErrorMessage() {
        return "Name must be between " + MIN_LENGTH + " and " + MAX_LENGTH +
                " characters and contain only letters, spaces, hyphens, and apostrophes";
    }
}

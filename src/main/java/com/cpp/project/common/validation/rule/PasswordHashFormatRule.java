package com.cpp.project.common.validation.rule;

import com.cpp.project.common.validation.entity.ValidationRule;

public class PasswordHashFormatRule implements ValidationRule<String> {
    @Override
    public boolean isValid(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            return false;
        }

        // Check if it's valid Base64
        try {
            java.util.Base64.getDecoder().decode(passwordHash);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return "Password hash must be a valid Base64 encoded string";
    }
}

package com.cpp.project.common.validation.rule;

import com.cpp.project.common.validation.entity.ValidationRule;

public class PasswordStrengthRule implements ValidationRule<String> {
    private final int minLength;
    private final boolean requireUppercase;
    private final boolean requireLowercase;
    private final boolean requireDigit;
    private final boolean requireSpecialChar;

    public PasswordStrengthRule(int minLength, boolean requireUppercase,
                                boolean requireLowercase, boolean requireDigit,
                                boolean requireSpecialChar) {
        this.minLength = minLength;
        this.requireUppercase = requireUppercase;
        this.requireLowercase = requireLowercase;
        this.requireDigit = requireDigit;
        this.requireSpecialChar = requireSpecialChar;
    }

    public static PasswordStrengthRule standard() {
        return new PasswordStrengthRule(8, true, true, true, true);
    }

    public static PasswordStrengthRule basic() {
        return new PasswordStrengthRule(6, false, false, false, false);
    }

    public static PasswordStrengthRule login() {
        return new PasswordStrengthRule(1, false, false, false, false);
    }

    @Override
    public boolean isValid(String password) {
        if (password == null || password.length() < minLength) {
            return false;
        }

        if (requireUppercase && !password.matches(".*[A-Z].*")) {
            return false;
        }

        if (requireLowercase && !password.matches(".*[a-z].*")) {
            return false;
        }

        if (requireDigit && !password.matches(".*\\d.*")) {
            return false;
        }

        return !requireSpecialChar || password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    }

    @Override
    public String getErrorMessage() {
        StringBuilder msg = new StringBuilder("Password must be at least " + minLength + " characters");
        if (requireUppercase) msg.append(", contain uppercase letter");
        if (requireLowercase) msg.append(", contain lowercase letter");
        if (requireDigit) msg.append(", contain digit");
        if (requireSpecialChar) msg.append(", contain special character");
        return msg.toString();
    }
}

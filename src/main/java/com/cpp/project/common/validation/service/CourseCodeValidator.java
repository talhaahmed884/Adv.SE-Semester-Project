package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

/**
 * Validator for course codes
 * Course codes should be 2-20 characters, alphanumeric with dashes/underscores
 */
public class CourseCodeValidator extends Validator<String> {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 20;
    private static final String CODE_PATTERN = "^[A-Z0-9_-]+$";

    @Override
    protected void performValidation(String code, ValidationResultBuilder resultBuilder) {
        if (code == null) {
            resultBuilder.addError("Course code cannot be null");
            return;
        }

        String trimmed = code.trim();

        if (trimmed.isEmpty()) {
            resultBuilder.addError("Course code cannot be empty");
            return;
        }

        if (trimmed.length() < MIN_LENGTH) {
            resultBuilder.addError("Course code must be at least " + MIN_LENGTH + " characters");
        }

        if (trimmed.length() > MAX_LENGTH) {
            resultBuilder.addError("Course code must not exceed " + MAX_LENGTH + " characters");
        }

        if (!trimmed.matches(CODE_PATTERN)) {
            resultBuilder.addError("Course code must contain only uppercase letters, numbers, dashes, and underscores");
        }
    }
}

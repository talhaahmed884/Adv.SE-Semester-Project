package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

/**
 * Validator for course names
 */
public class CourseNameValidator extends Validator<String> {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 255;

    @Override
    protected void performValidation(String name, ValidationResultBuilder resultBuilder) {
        if (name == null) {
            resultBuilder.addError("Course name cannot be null");
            return;
        }

        String trimmed = name.trim();

        if (trimmed.isEmpty()) {
            resultBuilder.addError("Course name cannot be empty");
            return;
        }

        if (trimmed.length() < MIN_LENGTH) {
            resultBuilder.addError("Course name must be at least " + MIN_LENGTH + " characters");
        }

        if (trimmed.length() > MAX_LENGTH) {
            resultBuilder.addError("Course name must not exceed " + MAX_LENGTH + " characters");
        }
    }
}

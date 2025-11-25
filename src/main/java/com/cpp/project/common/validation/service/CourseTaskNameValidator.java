package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

/**
 * Validator for course task names
 */
public class CourseTaskNameValidator extends Validator<String> {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 255;

    @Override
    protected void performValidation(String name, ValidationResultBuilder resultBuilder) {
        if (name == null) {
            resultBuilder.addError("Task name cannot be null");
            return;
        }

        String trimmed = name.trim();

        if (trimmed.isEmpty()) {
            resultBuilder.addError("Task name cannot be empty");
            return;
        }

        if (trimmed.length() < MIN_LENGTH) {
            resultBuilder.addError("Task name must be at least " + MIN_LENGTH + " character");
        }

        if (trimmed.length() > MAX_LENGTH) {
            resultBuilder.addError("Task name must not exceed " + MAX_LENGTH + " characters");
        }
    }
}

package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

/**
 * Validator for todo list task descriptions
 */
public class ToDoListTaskDescriptionValidator extends Validator<String> {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 5000;

    @Override
    protected void performValidation(String description, ValidationResultBuilder resultBuilder) {
        if (description == null) {
            resultBuilder.addError("Task description cannot be null");
            return;
        }

        String trimmed = description.trim();

        if (trimmed.isEmpty()) {
            resultBuilder.addError("Task description cannot be empty");
            return;
        }

        if (trimmed.length() < MIN_LENGTH) {
            resultBuilder.addError("Task description must be at least " + MIN_LENGTH + " character");
        }

        if (trimmed.length() > MAX_LENGTH) {
            resultBuilder.addError("Task description must not exceed " + MAX_LENGTH + " characters");
        }
    }
}

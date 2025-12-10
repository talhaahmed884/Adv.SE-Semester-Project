package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

import java.time.Instant;

/**
 * Validator for task deadlines (used by both Course tasks and ToDoList tasks)
 * Deadline is optional for ToDoList tasks (can be null)
 * If provided, deadline must be in the future or equal to now (UTC-based comparison)
 */
public class ToDoListTaskDeadlineValidator extends Validator<Instant> {
    @Override
    protected void performValidation(Instant deadline, ValidationResultBuilder resultBuilder) {
        // Deadline is optional - null is allowed
        if (deadline == null) {
            return; // Valid - no deadline specified
        }

        // If deadline is provided, it must not be in the past
        Instant now = Instant.now(); // Always UTC
        if (deadline.isBefore(now) && !deadline.equals(now)) {
            resultBuilder.addError("Deadline cannot be in the past");
        }
    }
}

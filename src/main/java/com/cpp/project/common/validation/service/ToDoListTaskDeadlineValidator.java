package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

import java.time.Instant;

/**
 * Validator for task deadlines (used by both Course tasks and ToDoList tasks)
 * Deadline must be in the future or equal to now (UTC-based comparison)
 */
public class ToDoListTaskDeadlineValidator extends Validator<Instant> {
    @Override
    protected void performValidation(Instant deadline, ValidationResultBuilder resultBuilder) {
        if (deadline == null) {
            resultBuilder.addError("Deadline cannot be null");
            return;
        }

        Instant now = Instant.now(); // Always UTC
        if (deadline.isBefore(now) && !deadline.equals(now)) {
            resultBuilder.addError("Deadline cannot be in the past");
        }
    }
}

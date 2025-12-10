package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

import java.time.Instant;

/**
 * Validator for course task deadline
 * Deadline must be in the future (UTC-based comparison)
 */
public class CourseTaskDeadlineValidator extends Validator<Instant> {
    @Override
    protected void performValidation(Instant deadline, ValidationResultBuilder resultBuilder) {
        if (deadline == null) {
            resultBuilder.addError("Deadline cannot be null");
            return;
        }

        Instant now = Instant.now(); // Always UTC
        if (deadline.isBefore(now)) {
            resultBuilder.addError("Deadline cannot be in the past");
        }
    }
}

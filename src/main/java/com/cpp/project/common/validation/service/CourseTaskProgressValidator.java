package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

/**
 * Validator for course task progress
 * Progress must be between 0 and 100
 */
public class CourseTaskProgressValidator extends Validator<Integer> {
    @Override
    protected void performValidation(Integer progress, ValidationResultBuilder resultBuilder) {
        if (progress == null) {
            resultBuilder.addError("Progress cannot be null");
            return;
        }

        if (progress < 0) {
            resultBuilder.addError("Progress cannot be negative");
        }

        if (progress > 100) {
            resultBuilder.addError("Progress cannot exceed 100");
        }
    }
}

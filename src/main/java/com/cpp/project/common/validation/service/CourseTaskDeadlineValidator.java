package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

import java.util.Date;

/**
 * Validator for course task deadline
 * Deadline must be in the future
 */
public class CourseTaskDeadlineValidator extends Validator<Date> {
    @Override
    protected void performValidation(Date deadline, ValidationResultBuilder resultBuilder) {
        if (deadline == null) {
            resultBuilder.addError("Deadline cannot be null");
            return;
        }

        Date now = new Date();
        if (deadline.before(now)) {
            resultBuilder.addError("Deadline cannot be in the past");
        }
    }
}

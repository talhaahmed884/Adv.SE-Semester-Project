package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.entity.Validator;

import java.util.Date;

/**
 * Validator for task deadlines (used by both Course tasks and ToDoList tasks)
 * Deadline must be in the future or equal to now
 */
public class ToDoListTaskDeadlineValidator extends Validator<Date> {
    @Override
    protected void performValidation(Date deadline, ValidationResultBuilder resultBuilder) {
        if (deadline == null) {
            resultBuilder.addError("Deadline cannot be null");
            return;
        }

        Date now = new Date();
        if (deadline.before(now) && !deadline.equals(now)) {
            resultBuilder.addError("Deadline cannot be in the past");
        }
    }
}

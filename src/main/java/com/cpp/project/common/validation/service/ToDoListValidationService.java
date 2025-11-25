package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import org.springframework.stereotype.Service;

/**
 * Service for ToDoList domain validation
 * Facade Pattern - Provides simple interface for all todo list-related validations
 */
@Service
public class ToDoListValidationService {
    private final ToDoListNameValidator nameValidator = new ToDoListNameValidator();
    private final ToDoListTaskDescriptionValidator descriptionValidator = new ToDoListTaskDescriptionValidator();
    private final ToDoListTaskDeadlineValidator deadlineValidator = new ToDoListTaskDeadlineValidator();

    /**
     * Validate todo list name
     */
    public ValidationResult validateName(String name) {
        return nameValidator.validate(name);
    }

    /**
     * Validate task description
     */
    public ValidationResult validateTaskDescription(String description) {
        return descriptionValidator.validate(description);
    }

    /**
     * Validate task deadline
     */
    public ValidationResult validateTaskDeadline(java.util.Date deadline) {
        return deadlineValidator.validate(deadline);
    }
}

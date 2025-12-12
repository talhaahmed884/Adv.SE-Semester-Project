package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for Timer domain validation
 * Facade Pattern - Provides simple interface for all timer-related validations
 */
@Service
public class TimerValidationService {
    private final UUIDValidator uuidValidator = new UUIDValidator();

    /**
     * Validate timer ID
     *
     * @param timerId The timer ID to validate
     * @return ValidationResult
     */
    public ValidationResult validateTimerId(UUID timerId) {
        return uuidValidator.validate(timerId, "Timer ID");
    }

    /**
     * Validate course task ID
     *
     * @param taskId The course task ID to validate
     * @return ValidationResult
     */
    public ValidationResult validateCourseTaskId(UUID taskId) {
        return uuidValidator.validate(taskId, "Course Task ID");
    }

    /**
     * Validate user ID
     *
     * @param userId The user ID to validate
     * @return ValidationResult
     */
    public ValidationResult validateUserId(UUID userId) {
        return uuidValidator.validate(userId, "User ID");
    }
}

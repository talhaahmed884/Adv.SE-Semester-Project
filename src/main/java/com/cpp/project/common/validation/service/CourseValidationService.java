package com.cpp.project.common.validation.service;

import com.cpp.project.common.validation.entity.ValidationResult;
import org.springframework.stereotype.Service;

/**
 * Service for Course domain validation
 * Facade Pattern - Provides simple interface for all course-related validations
 */
@Service
public class CourseValidationService {
    private final CourseCodeValidator courseCodeValidator = new CourseCodeValidator();
    private final CourseNameValidator courseNameValidator = new CourseNameValidator();
    private final CourseTaskNameValidator taskNameValidator = new CourseTaskNameValidator();
    private final CourseTaskProgressValidator progressValidator = new CourseTaskProgressValidator();

    /**
     * Validate course code
     */
    public ValidationResult validateCourseCode(String code) {
        return courseCodeValidator.validate(code);
    }

    /**
     * Validate course name
     */
    public ValidationResult validateCourseName(String name) {
        return courseNameValidator.validate(name);
    }

    /**
     * Validate task name
     */
    public ValidationResult validateTaskName(String name) {
        return taskNameValidator.validate(name);
    }

    /**
     * Validate task progress
     */
    public ValidationResult validateTaskProgress(Integer progress) {
        return progressValidator.validate(progress);
    }
}

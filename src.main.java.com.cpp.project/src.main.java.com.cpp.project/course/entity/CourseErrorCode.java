package com.cpp.project.course.entity;

import com.cpp.project.common.exception.entity.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Error codes for Course domain
 */
public enum CourseErrorCode implements ErrorCode {
    // Course errors
    COURSE_NOT_FOUND("COURSE_001", "Course not found with %s: %s", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_EXISTS("COURSE_002", "Course with code '%s' already exists", HttpStatus.CONFLICT),
    INVALID_COURSE_CODE("COURSE_003", "Invalid course code: %s", HttpStatus.BAD_REQUEST),
    INVALID_COURSE_NAME("COURSE_004", "Invalid course name: %s", HttpStatus.BAD_REQUEST),
    COURSE_CREATION_FAILED("COURSE_005", "Failed to create course: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    COURSE_UPDATE_FAILED("COURSE_006", "Failed to update course: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    COURSE_DELETION_FAILED("COURSE_007", "Failed to delete course: %s", HttpStatus.INTERNAL_SERVER_ERROR),

    // Course Task errors
    TASK_NOT_FOUND("COURSE_TASK_001", "Task not found with id: %s", HttpStatus.NOT_FOUND),
    INVALID_TASK_NAME("COURSE_TASK_002", "Invalid task name: %s", HttpStatus.BAD_REQUEST),
    INVALID_TASK_PROGRESS("COURSE_TASK_003", "Invalid progress value: %d. Must be between 0 and 100", HttpStatus.BAD_REQUEST),
    TASK_CREATION_FAILED("COURSE_TASK_004", "Failed to create task: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    TASK_UPDATE_FAILED("COURSE_TASK_005", "Failed to update task: %s", HttpStatus.INTERNAL_SERVER_ERROR),

    // Validation errors
    INVALID_COURSE_DATA("COURSE_VAL_001", "Invalid course data: %s", HttpStatus.BAD_REQUEST),
    INVALID_TASK_DATA("COURSE_VAL_002", "Invalid task data: %s", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    CourseErrorCode(String code, String messageTemplate, HttpStatus httpStatus) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

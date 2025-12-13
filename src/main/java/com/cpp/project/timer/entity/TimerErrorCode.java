package com.cpp.project.timer.entity;

import com.cpp.project.common.exception.entity.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Error codes for Timer domain
 */
public enum TimerErrorCode implements ErrorCode {
    // Timer session errors
    TIMER_NOT_FOUND("TIMER_001", "Timer not found with id: %s", HttpStatus.NOT_FOUND),
    TIMER_ALREADY_RUNNING("TIMER_002", "Timer already running for task %s", HttpStatus.CONFLICT),
    TIMER_NOT_RUNNING("TIMER_003", "Timer is not running for id: %s", HttpStatus.BAD_REQUEST),
    TIMER_START_FAILED("TIMER_004", "Failed to start timer: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    TIMER_STOP_FAILED("TIMER_005", "Failed to stop timer: %s", HttpStatus.INTERNAL_SERVER_ERROR),

    // Task validation errors
    INVALID_TASK_ID("TIMER_006", "Invalid course task id: %s", HttpStatus.BAD_REQUEST),
    TASK_NOT_FOUND("TIMER_007", "Course task not found with id: %s", HttpStatus.NOT_FOUND),

    // User validation errors
    INVALID_USER_ID("TIMER_008", "Invalid user id: %s", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_TIMER_ACCESS("TIMER_009", "User %s is not authorized to access timer %s", HttpStatus.FORBIDDEN),

    // Timer retrieval errors
    NO_TIMERS_FOUND("TIMER_010", "No timers found for task: %s", HttpStatus.NOT_FOUND),
    TIMER_QUERY_FAILED("TIMER_011", "Failed to query timers: %s", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    TimerErrorCode(String code, String messageTemplate, HttpStatus httpStatus) {
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

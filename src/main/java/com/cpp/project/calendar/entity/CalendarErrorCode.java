package com.cpp.project.calendar.entity;

import com.cpp.project.common.exception.entity.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Error codes for Calendar domain
 */
public enum CalendarErrorCode implements ErrorCode {
    INVALID_DATE("CALENDAR_001", "Invalid date parameters: %s", HttpStatus.BAD_REQUEST),
    INVALID_MONTH("CALENDAR_002", "Month must be between 1 and 12", HttpStatus.BAD_REQUEST),
    INVALID_YEAR("CALENDAR_003", "Year must be between 1900 and 2100", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("CALENDAR_004", "User not found: %s", HttpStatus.NOT_FOUND);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    CalendarErrorCode(String code, String messageTemplate, HttpStatus httpStatus) {
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

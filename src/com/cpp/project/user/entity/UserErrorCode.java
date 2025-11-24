package com.cpp.project.user.entity;

import com.cpp.project.common.exception.entity.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_001", "User not found with %s: %s", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_002", "User already exists with email: %s", HttpStatus.CONFLICT),
    INVALID_USER_DATA("USER_003", "Invalid user data: %s", HttpStatus.BAD_REQUEST),
    USER_CREATION_FAILED("USER_004", "Failed to create user: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_UPDATE_FAILED("USER_005", "Failed to update user: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_DELETION_FAILED("USER_006", "Failed to delete user: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_EMAIL_FORMAT("USER_007", "Invalid email format: %s", HttpStatus.BAD_REQUEST),
    INVALID_NAME("USER_008", "Invalid name: name cannot be null or empty", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_IN_USE("USER_009", "Email already in use: %s", HttpStatus.CONFLICT);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    UserErrorCode(String code, String messageTemplate, HttpStatus httpStatus) {
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

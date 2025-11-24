package com.cpp.project.user.entity;

import com.cpp.project.common.exception.entity.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthenticationErrorCode implements ErrorCode {
    AUTHENTICATION_FAILED("AUTH_001", "Authentication failed for user: %s", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("AUTH_002", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    USER_NOT_AUTHENTICATED("AUTH_003", "User is not authenticated", HttpStatus.UNAUTHORIZED),
    SESSION_EXPIRED("AUTH_004", "Session has expired", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    AuthenticationErrorCode(String code, String messageTemplate, HttpStatus httpStatus) {
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

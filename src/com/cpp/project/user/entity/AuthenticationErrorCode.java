package com.cpp.project.user.entity;

import com.cpp.project.exception.entity.ErrorCode;

public enum AuthenticationErrorCode implements ErrorCode {
    AUTHENTICATION_FAILED("AUTH_001", "Authentication failed for user: %s"),
    INVALID_CREDENTIALS("AUTH_002", "Invalid email or password"),
    USER_NOT_AUTHENTICATED("AUTH_003", "User is not authenticated"),
    SESSION_EXPIRED("AUTH_004", "Session has expired");

    private final String code;
    private final String messageTemplate;

    AuthenticationErrorCode(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}


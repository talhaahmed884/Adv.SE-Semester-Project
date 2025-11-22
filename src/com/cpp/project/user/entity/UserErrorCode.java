package com.cpp.project.user.entity;

import com.cpp.project.exception.entity.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_001", "User not found with %s: %s"),
    USER_ALREADY_EXISTS("USER_002", "User already exists with email: %s"),
    INVALID_USER_DATA("USER_003", "Invalid user data: %s"),
    USER_CREATION_FAILED("USER_004", "Failed to create user: %s"),
    USER_UPDATE_FAILED("USER_005", "Failed to update user: %s"),
    USER_DELETION_FAILED("USER_006", "Failed to delete user: %s"),
    INVALID_EMAIL_FORMAT("USER_007", "Invalid email format: %s"),
    INVALID_NAME("USER_008", "Invalid name: name cannot be null or empty"),
    EMAIL_ALREADY_IN_USE("USER_009", "Email already in use: %s");

    private final String code;
    private final String messageTemplate;

    UserErrorCode(String code, String messageTemplate) {
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


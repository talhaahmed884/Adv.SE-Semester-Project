package com.cpp.project.user_credential.entity;

import com.cpp.project.exception.entity.ErrorCode;

public enum UserCredentialErrorCode implements ErrorCode {
    CREDENTIAL_NOT_FOUND("CRED_001", "Credentials not found for user: %s"),
    INVALID_PASSWORD("CRED_002", "Invalid password provided"),
    PASSWORD_HASH_FAILED("CRED_003", "Failed to hash password: %s"),
    PASSWORD_VERIFICATION_FAILED("CRED_004", "Failed to verify password: %s"),
    CREDENTIAL_CREATION_FAILED("CRED_005", "Failed to create credentials: %s"),
    CREDENTIAL_UPDATE_FAILED("CRED_006", "Failed to update credentials: %s"),
    INVALID_ALGORITHM("CRED_007", "Invalid hashing algorithm: %s"),
    PASSWORD_REQUIRED("CRED_008", "Password is required"),
    PASSWORD_HASH_EMPTY("CRED_009", "Password hash cannot be null or empty");

    private final String code;
    private final String messageTemplate;

    UserCredentialErrorCode(String code, String messageTemplate) {
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


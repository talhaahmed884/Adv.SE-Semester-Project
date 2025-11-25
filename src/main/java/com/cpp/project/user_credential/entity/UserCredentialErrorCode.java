package com.cpp.project.user_credential.entity;

import com.cpp.project.common.exception.entity.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserCredentialErrorCode implements ErrorCode {
    CREDENTIAL_NOT_FOUND("CRED_001", "Credentials not found for user: %s", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("CRED_002", "Invalid password provided", HttpStatus.BAD_REQUEST),
    PASSWORD_HASH_FAILED("CRED_003", "Failed to hash password: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_VERIFICATION_FAILED("CRED_004", "Failed to verify password: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    CREDENTIAL_CREATION_FAILED("CRED_005", "Failed to create credentials: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    CREDENTIAL_UPDATE_FAILED("CRED_006", "Failed to update credentials: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ALGORITHM("CRED_007", "Invalid hashing algorithm: %s", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED("CRED_008", "Password is required", HttpStatus.BAD_REQUEST),
    PASSWORD_HASH_EMPTY("CRED_009", "Password hash cannot be null or empty", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_HASH("CRED_010", "Invalid password hash: %s", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    UserCredentialErrorCode(String code, String messageTemplate, HttpStatus httpStatus) {
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

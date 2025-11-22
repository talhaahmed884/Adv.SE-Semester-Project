package com.cpp.project.user_credential.entity;

import com.cpp.project.exception.entity.BaseException;
import com.cpp.project.exception.entity.ErrorCode;

public class UserCredentialException extends BaseException {
    public UserCredentialException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public UserCredentialException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
}


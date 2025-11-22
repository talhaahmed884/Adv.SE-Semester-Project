package com.cpp.project.user.entity;

import com.cpp.project.exception.entity.BaseException;
import com.cpp.project.exception.entity.ErrorCode;

public class AuthenticationException extends BaseException {
    public AuthenticationException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public AuthenticationException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
}

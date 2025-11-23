package com.cpp.project.user.entity;

import com.cpp.project.common.exception.entity.BaseException;
import com.cpp.project.common.exception.entity.ErrorCode;

public class UserException extends BaseException {
    public UserException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public UserException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
}


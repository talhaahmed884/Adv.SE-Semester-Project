package com.cpp.project.exception.entity;

// Base exception class using Template Method Pattern
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;

    protected BaseException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
        this.args = args;
    }

    protected BaseException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getMessage(args), cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getCode() {
        return errorCode.getCode();
    }
}

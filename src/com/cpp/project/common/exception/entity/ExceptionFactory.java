package com.cpp.project.common.exception.entity;

// Exception Factory using Factory Pattern
public class ExceptionFactory {

    public static <T extends BaseException> T create(
            Class<T> exceptionClass,
            ErrorCode errorCode,
            Object... args) {
        try {
            return exceptionClass
                    .getDeclaredConstructor(ErrorCode.class, Object[].class)
                    .newInstance(errorCode, args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create exception instance", e);
        }
    }

    public static <T extends BaseException> T create(
            Class<T> exceptionClass,
            ErrorCode errorCode,
            Throwable cause,
            Object... args) {
        try {
            return exceptionClass
                    .getDeclaredConstructor(ErrorCode.class, Throwable.class, Object[].class)
                    .newInstance(errorCode, cause, args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create exception instance", e);
        }
    }
}


package com.cpp.project.timer.entity;

import com.cpp.project.common.exception.entity.BaseException;

/**
 * Exception class for Timer domain
 * Extends BaseException following project's exception hierarchy
 */
public class TimerException extends BaseException {
    public TimerException(TimerErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public TimerException(TimerErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
}

package com.cpp.project.calendar.entity;

import com.cpp.project.common.exception.entity.BaseException;
import com.cpp.project.common.exception.entity.ErrorCode;
import com.cpp.project.course.entity.CourseErrorCode;

/**
 * Exception class for Calendar domain
 */
public class CalendarException extends BaseException {
    public CalendarException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public CalendarException(CourseErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
}

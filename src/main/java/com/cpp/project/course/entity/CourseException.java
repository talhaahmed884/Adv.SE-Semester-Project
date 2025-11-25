package com.cpp.project.course.entity;

import com.cpp.project.common.exception.entity.BaseException;

/**
 * Exception class for Course domain
 * Extends BaseException following project's exception hierarchy
 */
public class CourseException extends BaseException {
    public CourseException(CourseErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public CourseException(CourseErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
}

package com.cpp.project.common.exception.entity;

import org.springframework.http.HttpStatus;

// Interface for error codes using Strategy Pattern
public interface ErrorCode {
    String getCode();

    String getMessageTemplate();

    HttpStatus getHttpStatus();

    default String getMessage(Object... args) {
        return String.format(getMessageTemplate(), args);
    }
}

package com.cpp.project.exception.entity;

// Interface for error codes using Strategy Pattern
public interface ErrorCode {
    String getCode();

    String getMessageTemplate();

    default String getMessage(Object... args) {
        return String.format(getMessageTemplate(), args);
    }
}

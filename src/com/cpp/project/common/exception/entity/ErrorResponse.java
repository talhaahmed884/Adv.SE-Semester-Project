package com.cpp.project.common.exception.entity;

import java.time.LocalDateTime;
import java.util.List;

// Builder Pattern for error responses
public class ErrorResponse {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;
    private final List<String> details;
    private final String path;

    protected ErrorResponse(ErrorResponseBuilder builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.timestamp = builder.timestamp;
        this.details = builder.details;
        this.path = builder.path;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getDetails() {
        return details;
    }

    public String getPath() {
        return path;
    }
}


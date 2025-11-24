package com.cpp.project.common.exception.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Error response with field-level errors
public class ErrorResponseDTO {
    private final String code;
    private final String message;
    private final int statusCode;
    private final String path;
    private final LocalDateTime timestamp;
    private final List<String> details;
    private final Map<String, String> fieldErrors;

    protected ErrorResponseDTO(ErrorResponseDTOBuilder builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.statusCode = builder.statusCode;
        this.path = builder.path;
        this.timestamp = builder.timestamp;
        this.details = builder.details;
        this.fieldErrors = builder.fieldErrors;
    }

    public static ErrorResponseDTOBuilder builder() {
        return new ErrorResponseDTOBuilder();
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getDetails() {
        return details;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}

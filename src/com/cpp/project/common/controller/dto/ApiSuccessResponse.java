package com.cpp.project.common.controller.dto;

import java.time.LocalDateTime;

// Generic success response wrapper
public class ApiSuccessResponse<T> {
    private final T data;
    private final String message;
    private final int statusCode;
    private final LocalDateTime timestamp;

    protected ApiSuccessResponse(ApiSuccessResponseBuilder<T> builder) {
        this.data = builder.data;
        this.message = builder.message;
        this.statusCode = builder.statusCode;
        this.timestamp = builder.timestamp;
    }

    public static <T> ApiSuccessResponseBuilder<T> builder() {
        return new ApiSuccessResponseBuilder<>();
    }

    // Getters
    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

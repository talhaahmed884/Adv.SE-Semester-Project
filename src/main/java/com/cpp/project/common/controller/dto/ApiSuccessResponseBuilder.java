package com.cpp.project.common.controller.dto;

import java.time.LocalDateTime;

public class ApiSuccessResponseBuilder<T> {
    protected T data;
    protected String message;
    protected int statusCode = 200;
    protected LocalDateTime timestamp = LocalDateTime.now();

    public ApiSuccessResponseBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    public ApiSuccessResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public ApiSuccessResponseBuilder<T> statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ApiSuccessResponseBuilder<T> timestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ApiSuccessResponse<T> build() {
        return new ApiSuccessResponse<>(this);
    }
}

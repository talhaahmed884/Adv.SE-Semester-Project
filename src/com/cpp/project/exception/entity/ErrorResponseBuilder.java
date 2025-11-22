package com.cpp.project.exception.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ErrorResponseBuilder {
    protected String code;
    protected String message;
    protected LocalDateTime timestamp = LocalDateTime.now();
    protected List<String> details = new ArrayList<>();
    protected String path;

    public ErrorResponseBuilder code(String code) {
        this.code = code;
        return this;
    }

    public ErrorResponseBuilder message(String message) {
        this.message = message;
        return this;
    }

    public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ErrorResponseBuilder addDetail(String detail) {
        this.details.add(detail);
        return this;
    }

    public ErrorResponseBuilder details(List<String> details) {
        this.details = details;
        return this;
    }

    public ErrorResponseBuilder path(String path) {
        this.path = path;
        return this;
    }

    public ErrorResponse build() {
        return new ErrorResponse(this);
    }
}

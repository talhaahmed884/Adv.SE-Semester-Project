package com.cpp.project.common.exception.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorResponseDTOBuilder {
    protected String code;
    protected String message;
    protected int statusCode;
    protected String path;
    protected LocalDateTime timestamp = LocalDateTime.now();
    protected List<String> details = new ArrayList<>();
    protected Map<String, String> fieldErrors = new HashMap<>();

    public ErrorResponseDTOBuilder code(String code) {
        this.code = code;
        return this;
    }

    public ErrorResponseDTOBuilder message(String message) {
        this.message = message;
        return this;
    }

    public ErrorResponseDTOBuilder statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ErrorResponseDTOBuilder path(String path) {
        this.path = path;
        return this;
    }

    public ErrorResponseDTOBuilder timestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ErrorResponseDTOBuilder addDetail(String detail) {
        this.details.add(detail);
        return this;
    }

    public ErrorResponseDTOBuilder details(List<String> details) {
        this.details = details;
        return this;
    }

    public ErrorResponseDTOBuilder addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
        return this;
    }

    public ErrorResponseDTOBuilder fieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
        return this;
    }

    public ErrorResponseDTO build() {
        return new ErrorResponseDTO(this);
    }
}

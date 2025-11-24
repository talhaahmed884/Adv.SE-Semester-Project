package com.cpp.project.common.validation.entity;

import java.util.ArrayList;
import java.util.List;

// Builder Pattern for validation results
public class ValidationResult {
    private final boolean valid;
    private final List<String> errors;

    protected ValidationResult(ValidationResultBuilder builder) {
        this.valid = builder.errors.isEmpty();
        this.errors = new ArrayList<>(builder.errors);
    }

    public static ValidationResultBuilder builder() {
        return new ValidationResultBuilder();
    }

    public static ValidationResult valid() {
        return new ValidationResultBuilder().build();
    }

    public static ValidationResult invalid(String error) {
        return new ValidationResultBuilder().addError(error).build();
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public String getFirstError() {
        return errors.isEmpty() ? null : errors.getFirst();
    }
}

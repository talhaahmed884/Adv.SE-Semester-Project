package com.cpp.project.common.validation.entity;

import java.util.ArrayList;
import java.util.List;

public class ValidationResultBuilder {
    protected final List<String> errors = new ArrayList<>();

    public ValidationResultBuilder addError(String error) {
        this.errors.add(error);
        return this;
    }

    public ValidationResultBuilder addErrors(List<String> errors) {
        this.errors.addAll(errors);
        return this;
    }

    public ValidationResult build() {
        return new ValidationResult(this);
    }
}

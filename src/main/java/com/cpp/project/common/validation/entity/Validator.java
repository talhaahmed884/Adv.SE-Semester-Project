package com.cpp.project.common.validation.entity;

// Template Method Pattern for validators
public abstract class Validator<T> {

    public ValidationResult validate(T value) {
        ValidationResultBuilder resultBuilder = ValidationResult.builder();

        performValidation(value, resultBuilder);

        return resultBuilder.build();
    }

    protected abstract void performValidation(T value, ValidationResultBuilder resultBuilder);

    protected void addErrorIf(boolean condition, String errorMessage, ValidationResultBuilder resultBuilder) {
        if (condition) {
            resultBuilder.addError(errorMessage);
        }
    }
}

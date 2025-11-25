package com.cpp.project.common.validation.entity;

// Chain of Responsibility Pattern for validation pipeline
public abstract class ValidationHandler<T> {
    protected ValidationHandler<T> next;

    public ValidationHandler<T> setNext(ValidationHandler<T> handler) {
        this.next = handler;
        return handler;
    }

    public ValidationResult handle(T value) {
        ValidationResultBuilder resultBuilder = ValidationResult.builder();

        // Perform this handler's validation
        ValidationResult currentResult = doValidate(value);
        if (!currentResult.isValid()) {
            resultBuilder.addErrors(currentResult.getErrors());
        }

        // Continue to next handler if exists
        if (next != null) {
            ValidationResult nextResult = next.handle(value);
            if (!nextResult.isValid()) {
                resultBuilder.addErrors(nextResult.getErrors());
            }
        }

        return resultBuilder.build();
    }

    protected abstract ValidationResult doValidate(T value);
}

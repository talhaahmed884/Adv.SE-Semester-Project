package com.cpp.project.common.validation.entity;

// Strategy Pattern for validation rules
public interface ValidationRule<T> {
    boolean isValid(T value);

    String getErrorMessage();
}

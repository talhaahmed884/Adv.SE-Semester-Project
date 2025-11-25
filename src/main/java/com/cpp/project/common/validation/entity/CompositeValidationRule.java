package com.cpp.project.common.validation.entity;

import java.util.ArrayList;
import java.util.List;

// Composite Pattern for combining multiple validation rules
public class CompositeValidationRule<T> implements ValidationRule<T> {
    private final List<ValidationRule<T>> rules = new ArrayList<>();
    private final String errorMessage;

    public CompositeValidationRule(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public CompositeValidationRule<T> addRule(ValidationRule<T> rule) {
        rules.add(rule);
        return this;
    }

    @Override
    public boolean isValid(T value) {
        return rules.stream().allMatch(rule -> rule.isValid(value));
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    public List<String> getAllErrorMessages(T value) {
        List<String> errors = new ArrayList<>();
        for (ValidationRule<T> rule : rules) {
            if (!rule.isValid(value)) {
                errors.add(rule.getErrorMessage());
            }
        }
        return errors;
    }
}

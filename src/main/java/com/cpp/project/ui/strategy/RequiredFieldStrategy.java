package com.cpp.project.ui.strategy;

/**
 * Validates that a field is not empty
 */
public class RequiredFieldStrategy implements ValidationStrategy {
    private final String fieldName;

    public RequiredFieldStrategy(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String validate(String input) {
        if (input == null || input.trim().isEmpty()) {
            return fieldName + " is required";
        }
        return null;
    }
}

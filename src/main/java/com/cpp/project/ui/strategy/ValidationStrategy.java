package com.cpp.project.ui.strategy;

/**
 * Strategy Pattern: Interface for validation strategies
 * Allows different validation rules to be swapped dynamically
 */
public interface ValidationStrategy {
    /**
     * Validate the input
     *
     * @return error message if invalid, null if valid
     */
    String validate(String input);
}

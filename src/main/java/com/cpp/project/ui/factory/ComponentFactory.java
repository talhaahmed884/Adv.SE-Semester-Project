package com.cpp.project.ui.factory;

import com.cpp.project.ui.component.DateInput;
import com.cpp.project.ui.component.FormField;

/**
 * Factory Pattern (Creational): Factory for creating UI components
 * Centralizes component creation logic
 */
public class ComponentFactory {
    /**
     * Create a text input field
     */
    public static FormField createTextField(String label) {
        return new FormField(label);
    }

    /**
     * Create a password field (masked input)
     */
    public static FormField createPasswordField(String label) {
        return new FormField(label, null, true);
    }

    /**
     * Create an email field with validation
     */
    public static FormField createEmailField(String label) {
        return new FormField(label, (c, current) -> {
            // Allow email characters
            return Character.isLetterOrDigit(c) || c == '@' || c == '.' || c == '_' || c == '-';
        }, false);
    }

    /**
     * Create a numeric field
     */
    public static FormField createNumericField(String label, int maxLength) {
        return new FormField(label, (c, current) -> {
            return Character.isDigit(c) && current.length() < maxLength;
        }, false);
    }

    /**
     * Create a date input component
     */
    public static DateInput createDateInput(String label) {
        return new DateInput(label);
    }
}

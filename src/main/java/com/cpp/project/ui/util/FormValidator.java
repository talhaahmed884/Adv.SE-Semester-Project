package com.cpp.project.ui.util;

import com.cpp.project.ui.component.DateInput;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;

/**
 * Form Validation Helper for UI states
 * <p>
 * This class reduces duplication in form validation logic across UI states
 * by providing common validation methods with consistent error messaging.
 */
public class FormValidator {

    private final MessagePanel messagePanel;

    /**
     * Create a form validator with the specified message panel
     *
     * @param messagePanel Message panel for displaying errors
     */
    public FormValidator(MessagePanel messagePanel) {
        this.messagePanel = messagePanel;
    }

    /**
     * Validate required field
     *
     * @param fieldName Display name of the field
     * @param value     Field value to validate
     * @return true if valid, false if validation failed (error message set in panel)
     */
    public boolean validateRequired(String fieldName, String value) {
        String error = new RequiredFieldStrategy(fieldName).validate(value);
        if (error != null) {
            messagePanel.setError(error);
            return false;
        }
        return true;
    }

    /**
     * Validate date input field
     *
     * @param dateInput DateInput component to validate
     * @param fieldName Display name of the field
     * @return true if valid, false if validation failed (error message set in panel)
     */
    public boolean validateDateInput(DateInput dateInput, String fieldName) {
        if (dateInput.getDate() == null) {
            String error = dateInput.getErrorMessage();
            String message = error != null && !error.isEmpty() ? error : fieldName + " is required";
            messagePanel.setError(message);
            return false;
        }
        return true;
    }

    /**
     * Validate optional date input field (allows null)
     *
     * @param dateInput DateInput component to validate
     * @return true if valid (including null/empty), false if validation failed
     */
    public boolean validateOptionalDateInput(DateInput dateInput) {
        // If empty, it's valid (optional field)
        if (dateInput.isEmpty()) {
            return true;
        }

        // If not empty, must be valid date
        if (dateInput.getDate() == null) {
            String error = dateInput.getErrorMessage();
            messagePanel.setError(error != null && !error.isEmpty() ? error : "Invalid date format");
            return false;
        }
        return true;
    }

    /**
     * Validate non-empty string field
     *
     * @param fieldName Display name of the field
     * @param value     Field value to validate
     * @return true if not empty, false if empty (error message set in panel)
     */
    public boolean validateNotEmpty(String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            messagePanel.setError(fieldName + " cannot be empty");
            return false;
        }
        return true;
    }

    /**
     * Validate minimum length
     *
     * @param fieldName Display name of the field
     * @param value     Field value to validate
     * @param minLength Minimum required length
     * @return true if meets minimum length, false otherwise
     */
    public boolean validateMinLength(String fieldName, String value, int minLength) {
        if (value == null || value.trim().length() < minLength) {
            messagePanel.setError(fieldName + " must be at least " + minLength + " characters");
            return false;
        }
        return true;
    }

    /**
     * Validate maximum length
     *
     * @param fieldName Display name of the field
     * @param value     Field value to validate
     * @param maxLength Maximum allowed length
     * @return true if within maximum length, false otherwise
     */
    public boolean validateMaxLength(String fieldName, String value, int maxLength) {
        if (value != null && value.length() > maxLength) {
            messagePanel.setError(fieldName + " must not exceed " + maxLength + " characters");
            return false;
        }
        return true;
    }

    /**
     * Clear any error messages in the message panel
     */
    public void clearErrors() {
        messagePanel.clear();
    }
}

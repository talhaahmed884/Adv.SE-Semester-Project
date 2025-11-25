package com.cpp.project.common.sanitization.strategy;

import com.cpp.project.common.sanitization.entity.SanitizationStrategy;

/**
 * Strategy for sanitizing email addresses
 * - Trims leading/trailing whitespace
 * - Converts to lowercase (emails are case-insensitive per RFC 5321)
 */
public class EmailSanitizationStrategy implements SanitizationStrategy {

    @Override
    public String sanitize(String value) {
        if (value == null) {
            return null;
        }

        // Trim whitespace and convert to lowercase
        return value.trim().toLowerCase();
    }
}

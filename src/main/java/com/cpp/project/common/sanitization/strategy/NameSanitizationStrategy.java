package com.cpp.project.common.sanitization.strategy;

import com.cpp.project.common.sanitization.entity.SanitizationStrategy;

/**
 * Strategy for sanitizing user names
 * - Trims leading/trailing whitespace
 * - Normalizes internal whitespace (multiple spaces to single space)
 */
public class NameSanitizationStrategy implements SanitizationStrategy {

    @Override
    public String sanitize(String value) {
        if (value == null) {
            return null;
        }

        // Trim leading/trailing whitespace
        String trimmed = value.trim();

        // Normalize internal whitespace (multiple spaces to single space)
        return trimmed.replaceAll("\\s+", " ");
    }
}

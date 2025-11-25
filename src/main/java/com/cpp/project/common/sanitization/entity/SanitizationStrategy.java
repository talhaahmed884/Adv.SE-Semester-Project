package com.cpp.project.common.sanitization.entity;

/**
 * Strategy Pattern - Defines interface for data sanitization strategies
 * Different implementations can normalize data in different ways
 */
public interface SanitizationStrategy {
    /**
     * Sanitizes the input value according to the strategy
     *
     * @param value The value to sanitize (can be null)
     * @return The sanitized value, or null if input was null
     */
    String sanitize(String value);
}

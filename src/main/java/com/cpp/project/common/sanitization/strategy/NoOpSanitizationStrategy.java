package com.cpp.project.common.sanitization.strategy;

import com.cpp.project.common.sanitization.entity.SanitizationStrategy;

/**
 * No-operation sanitization strategy
 * Returns the value as-is without any transformation
 * Useful for sensitive data like passwords that should not be normalized
 */
public class NoOpSanitizationStrategy implements SanitizationStrategy {

    @Override
    public String sanitize(String value) {
        return value;
    }
}

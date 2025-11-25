package com.cpp.project.common.sanitization.entity;

import com.cpp.project.common.sanitization.strategy.EmailSanitizationStrategy;
import com.cpp.project.common.sanitization.strategy.NameSanitizationStrategy;
import com.cpp.project.common.sanitization.strategy.NoOpSanitizationStrategy;

/**
 * Factory Pattern - Creates appropriate sanitization strategy based on field type
 */
public class SanitizationStrategyFactory {

    private static final EmailSanitizationStrategy EMAIL_STRATEGY = new EmailSanitizationStrategy();
    private static final NameSanitizationStrategy NAME_STRATEGY = new NameSanitizationStrategy();
    private static final NoOpSanitizationStrategy NOOP_STRATEGY = new NoOpSanitizationStrategy();

    /**
     * Get the appropriate sanitization strategy for a field type
     *
     * @param fieldType The type of field to sanitize
     * @return The appropriate sanitization strategy
     */
    public static SanitizationStrategy getStrategy(FieldType fieldType) {
        return switch (fieldType) {
            case EMAIL -> EMAIL_STRATEGY;
            case NAME -> NAME_STRATEGY;
            case PASSWORD -> NOOP_STRATEGY; // Don't normalize passwords
            case GENERIC -> NOOP_STRATEGY;
        };
    }

    public enum FieldType {
        EMAIL,
        NAME,
        PASSWORD,
        GENERIC
    }
}

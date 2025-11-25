package com.cpp.project.common.sanitization.service;

import com.cpp.project.common.sanitization.entity.SanitizationStrategy;
import com.cpp.project.common.sanitization.entity.SanitizationStrategyFactory;
import com.cpp.project.common.sanitization.entity.SanitizationStrategyFactory.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Facade Pattern - Provides a simple interface for data sanitization
 * Hides the complexity of strategy selection and application
 */
@Service
public class DataSanitizationService {
    private static final Logger logger = LoggerFactory.getLogger(DataSanitizationService.class);

    /**
     * Sanitize email address (trim + lowercase)
     */
    public String sanitizeEmail(String email) {
        return sanitize(email, FieldType.EMAIL);
    }

    /**
     * Sanitize user name (trim + normalize whitespace)
     */
    public String sanitizeName(String name) {
        return sanitize(name, FieldType.NAME);
    }

    /**
     * Sanitize password (no transformation)
     */
    public String sanitizePassword(String password) {
        return sanitize(password, FieldType.PASSWORD);
    }

    /**
     * Generic sanitization using specified strategy
     */
    public String sanitize(String value, FieldType fieldType) {
        if (value == null) {
            return null;
        }

        SanitizationStrategy strategy = SanitizationStrategyFactory.getStrategy(fieldType);
        String sanitized = strategy.sanitize(value);

        if (logger.isTraceEnabled() && !value.equals(sanitized)) {
            logger.trace("Sanitized {} field: '{}' -> '{}'", fieldType, value, sanitized);
        }

        return sanitized;
    }

    /**
     * Sanitize using a custom strategy
     */
    public String sanitize(String value, SanitizationStrategy strategy) {
        if (value == null) {
            return null;
        }
        return strategy.sanitize(value);
    }
}

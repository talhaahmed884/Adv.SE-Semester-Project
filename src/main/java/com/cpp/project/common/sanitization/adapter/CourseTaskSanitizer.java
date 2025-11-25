package com.cpp.project.common.sanitization.adapter;

import com.cpp.project.common.sanitization.service.DataSanitizationService;
import org.springframework.stereotype.Component;

/**
 * Adapter Pattern - Sanitizes course task parameters
 */
@Component
public class CourseTaskSanitizer {
    private final DataSanitizationService sanitizationService;

    public CourseTaskSanitizer(DataSanitizationService sanitizationService) {
        this.sanitizationService = sanitizationService;
    }

    /**
     * Sanitize task name (trim and normalize whitespace)
     */
    public String sanitizeName(String name) {
        return sanitizationService.sanitizeName(name);
    }

    /**
     * Sanitize task description (trim)
     */
    public String sanitizeDescription(String description) {
        if (description == null) {
            return null;
        }
        return description.trim();
    }
}

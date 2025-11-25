package com.cpp.project.common.sanitization.adapter;

import com.cpp.project.common.sanitization.service.DataSanitizationService;
import org.springframework.stereotype.Component;

/**
 * Adapter Pattern - Sanitizes course creation request parameters
 */
@Component
public class CreateCourseRequestSanitizer {
    private final DataSanitizationService sanitizationService;

    public CreateCourseRequestSanitizer(DataSanitizationService sanitizationService) {
        this.sanitizationService = sanitizationService;
    }

    /**
     * Sanitize course code (trim and uppercase)
     */
    public String sanitizeCode(String code) {
        if (code == null) {
            return null;
        }
        // Trim and convert to uppercase
        return code.trim().toUpperCase();
    }

    /**
     * Sanitize course name (trim and normalize whitespace)
     */
    public String sanitizeName(String name) {
        return sanitizationService.sanitizeName(name);
    }
}

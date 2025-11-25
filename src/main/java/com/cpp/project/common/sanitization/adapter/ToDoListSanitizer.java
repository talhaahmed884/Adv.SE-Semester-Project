package com.cpp.project.common.sanitization.adapter;

import com.cpp.project.common.sanitization.service.DataSanitizationService;
import org.springframework.stereotype.Component;

/**
 * Adapter Pattern - Sanitizes todo list parameters
 */
@Component
public class ToDoListSanitizer {
    private final DataSanitizationService sanitizationService;

    public ToDoListSanitizer(DataSanitizationService sanitizationService) {
        this.sanitizationService = sanitizationService;
    }

    /**
     * Sanitize list name (trim and normalize whitespace)
     */
    public String sanitizeName(String name) {
        return sanitizationService.sanitizeName(name);
    }
}

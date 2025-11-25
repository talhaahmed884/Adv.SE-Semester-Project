package com.cpp.project.common.sanitization.adapter;

import org.springframework.stereotype.Component;

/**
 * Adapter Pattern - Sanitizes todo list task parameters
 */
@Component
public class ToDoListTaskSanitizer {
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

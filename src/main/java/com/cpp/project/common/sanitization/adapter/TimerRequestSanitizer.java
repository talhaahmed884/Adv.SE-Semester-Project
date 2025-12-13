package com.cpp.project.common.sanitization.adapter;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter Pattern - Sanitizes timer request parameters
 * UUIDs don't need sanitization, but included for consistency
 */
@Component
public class TimerRequestSanitizer {
    /**
     * Validate UUID format (UUIDs are already sanitized by type)
     *
     * @param uuid The UUID to sanitize
     * @return The same UUID (UUIDs are type-safe, no sanitization needed)
     */
    public UUID sanitizeUUID(UUID uuid) {
        return uuid; // UUIDs are type-safe, no sanitization needed
    }
}

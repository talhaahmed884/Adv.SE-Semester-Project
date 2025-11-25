package com.cpp.project.common.sanitization.adapter;

import com.cpp.project.common.sanitization.service.DataSanitizationService;
import com.cpp.project.user.dto.LoginRequestDTO;
import org.springframework.stereotype.Component;

/**
 * Adapter Pattern - Adapts raw LoginRequestDTO to sanitized version
 * Converts "dirty" input to "clean" normalized data
 */
@Component
public class LoginRequestSanitizer {
    private final DataSanitizationService sanitizationService;

    public LoginRequestSanitizer(DataSanitizationService sanitizationService) {
        this.sanitizationService = sanitizationService;
    }

    /**
     * Sanitizes a LoginRequestDTO by normalizing email
     * Note: Password is NOT normalized (kept as-is)
     *
     * @param request The raw request with potentially un-normalized data
     * @return A new DTO with sanitized fields
     */
    public LoginRequestDTO sanitize(LoginRequestDTO request) {
        if (request == null) {
            return null;
        }

        return new LoginRequestDTO(
                sanitizationService.sanitizeEmail(request.getEmail()),
                request.getPassword() // Don't sanitize password
        );
    }
}

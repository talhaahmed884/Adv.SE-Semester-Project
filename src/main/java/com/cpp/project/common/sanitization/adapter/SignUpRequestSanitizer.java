package com.cpp.project.common.sanitization.adapter;

import com.cpp.project.common.sanitization.service.DataSanitizationService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import org.springframework.stereotype.Component;

/**
 * Adapter Pattern - Adapts raw SignUpRequestDTO to sanitized version
 * Converts "dirty" input to "clean" normalized data
 */
@Component
public class SignUpRequestSanitizer {
    private final DataSanitizationService sanitizationService;

    public SignUpRequestSanitizer(DataSanitizationService sanitizationService) {
        this.sanitizationService = sanitizationService;
    }

    /**
     * Sanitizes a SignUpRequestDTO by normalizing all fields
     *
     * @param request The raw request with potentially un-normalized data
     * @return A new DTO with sanitized fields
     */
    public SignUpRequestDTO sanitize(SignUpRequestDTO request) {
        if (request == null) {
            return null;
        }

        return new SignUpRequestDTO(
                sanitizationService.sanitizeName(request.getName()),
                sanitizationService.sanitizeEmail(request.getEmail()),
                sanitizationService.sanitizePassword(request.getPassword())
        );
    }
}

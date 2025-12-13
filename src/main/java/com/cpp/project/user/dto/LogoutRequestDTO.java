package com.cpp.project.user.dto;

import java.util.UUID;

/**
 * Request DTO for user logout
 */
public class LogoutRequestDTO {
    private UUID userId;

    public LogoutRequestDTO() {
    }

    public LogoutRequestDTO(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    /**
     * Check if request has required fields
     *
     * @return true if request is empty or missing required fields
     */
    public boolean isEmpty() {
        return userId == null;
    }
}

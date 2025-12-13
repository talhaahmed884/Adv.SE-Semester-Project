package com.cpp.project.timer.dto;

import java.util.UUID;

/**
 * Request DTO for stopping a timer
 */
public class StopTimerRequestDTO {
    private UUID timerId;
    private UUID userId; // For authorization check

    public StopTimerRequestDTO() {
    }

    public StopTimerRequestDTO(UUID timerId, UUID userId) {
        this.timerId = timerId;
        this.userId = userId;
    }

    public UUID getTimerId() {
        return timerId;
    }

    public void setTimerId(UUID timerId) {
        this.timerId = timerId;
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
        return timerId == null || userId == null;
    }
}

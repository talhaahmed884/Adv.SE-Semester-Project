package com.cpp.project.timer.dto;

import java.util.UUID;

/**
 * Request DTO for starting a timer
 */
public class StartTimerRequestDTO {
    private UUID userId;
    private UUID courseTaskId;

    public StartTimerRequestDTO() {
    }

    public StartTimerRequestDTO(UUID userId, UUID courseTaskId) {
        this.userId = userId;
        this.courseTaskId = courseTaskId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCourseTaskId() {
        return courseTaskId;
    }

    public void setCourseTaskId(UUID courseTaskId) {
        this.courseTaskId = courseTaskId;
    }

    /**
     * Check if request has required fields
     *
     * @return true if request is empty or missing required fields
     */
    public boolean isEmpty() {
        return userId == null || courseTaskId == null;
    }
}

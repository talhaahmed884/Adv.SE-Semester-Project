package com.cpp.project.timer.dto;

import com.cpp.project.timer.entity.TimerStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Builder Pattern for TimerDTO
 */
public class TimerDTOBuilder {
    private UUID id;
    private UUID userId;
    private UUID courseTaskId;
    private Instant startTime;
    private Instant endTime;
    private long durationMillis;
    private TimerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TimerDTOBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public TimerDTOBuilder userId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public TimerDTOBuilder courseTaskId(UUID courseTaskId) {
        this.courseTaskId = courseTaskId;
        return this;
    }

    public TimerDTOBuilder startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public TimerDTOBuilder endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public TimerDTOBuilder durationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
        return this;
    }

    public TimerDTOBuilder status(TimerStatus status) {
        this.status = status;
        return this;
    }

    public TimerDTOBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public TimerDTOBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public TimerDTO build() {
        return new TimerDTO(id, userId, courseTaskId, startTime, endTime,
                durationMillis, status, createdAt, updatedAt);
    }
}

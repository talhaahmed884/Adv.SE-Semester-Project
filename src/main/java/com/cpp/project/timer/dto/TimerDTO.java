package com.cpp.project.timer.dto;

import com.cpp.project.timer.entity.TimerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for TaskTimer
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimerDTO {
    private UUID id;
    private UUID userId;
    private UUID courseTaskId;
    private Instant startTime;
    private Instant endTime;
    private long durationMillis;
    private TimerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder Pattern
    public static TimerDTOBuilder builder() {
        return new TimerDTOBuilder();
    }
}

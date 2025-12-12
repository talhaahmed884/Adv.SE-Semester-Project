package com.cpp.project.timer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TaskTimer entity
 * Represents a timer session for a course task
 * Multiple sessions can exist per task for accumulation
 */
@Entity
@Table(name = "task_timers")
@Getter
@Setter
@NoArgsConstructor
public class TaskTimer {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "course_task_id", nullable = false)
    private UUID courseTaskId;

    @Column(name = "start_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant startTime;

    @Column(name = "end_time", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant endTime;

    @Column(name = "duration_millis", nullable = false)
    private long durationMillis = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private TimerStatus status = TimerStatus.STOPPED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Builder Pattern
    public static TaskTimerBuilder builder() {
        return new TaskTimerBuilder();
    }

    /**
     * Stop the timer and calculate duration
     * Only works if timer is currently running
     */
    public void stop() {
        if (this.status != TimerStatus.RUNNING) {
            throw new TimerException(TimerErrorCode.TIMER_NOT_RUNNING, this.id);
        }

        this.endTime = Instant.now();
        this.durationMillis = Duration.between(startTime, endTime).toMillis();
        this.status = TimerStatus.STOPPED;
    }

    /**
     * Check if this timer is currently running
     *
     * @return true if status is RUNNING, false otherwise
     */
    public boolean isRunning() {
        return this.status == TimerStatus.RUNNING;
    }

    /**
     * Get duration as Duration object
     *
     * @return Duration object representing the timer duration
     */
    public Duration getDuration() {
        return Duration.ofMillis(durationMillis);
    }
}

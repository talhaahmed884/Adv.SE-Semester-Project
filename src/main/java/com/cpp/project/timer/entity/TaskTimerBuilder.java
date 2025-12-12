package com.cpp.project.timer.entity;

import java.time.Instant;
import java.util.UUID;

/**
 * Builder Pattern for TaskTimer entity
 */
public class TaskTimerBuilder {
    private UUID userId;
    private UUID courseTaskId;
    private Instant startTime;
    private Instant endTime;
    private long durationMillis = 0;
    private TimerStatus status = TimerStatus.STOPPED;

    public TaskTimerBuilder userId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public TaskTimerBuilder courseTaskId(UUID courseTaskId) {
        this.courseTaskId = courseTaskId;
        return this;
    }

    public TaskTimerBuilder startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public TaskTimerBuilder endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public TaskTimerBuilder durationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
        return this;
    }

    public TaskTimerBuilder status(TimerStatus status) {
        this.status = status;
        return this;
    }

    public TaskTimer build() {
        TaskTimer timer = new TaskTimer();
        timer.setUserId(userId);
        timer.setCourseTaskId(courseTaskId);
        timer.setStartTime(startTime);
        timer.setEndTime(endTime);
        timer.setDurationMillis(durationMillis);
        timer.setStatus(status != null ? status : TimerStatus.STOPPED);
        return timer;
    }
}

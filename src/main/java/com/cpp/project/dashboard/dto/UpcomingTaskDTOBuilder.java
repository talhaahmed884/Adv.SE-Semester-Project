package com.cpp.project.dashboard.dto;

import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.dashboard.entity.TaskUrgency;

import java.time.Instant;
import java.util.UUID;

/**
 * Builder for UpcomingTaskDTO
 * Builder Pattern - Provides fluent interface for constructing DTOs
 */
public class UpcomingTaskDTOBuilder {
    private UUID taskId;
    private String taskName;
    private Instant deadline;
    private TaskUrgency urgency;
    private String sourceType;
    private String sourceName;
    private TaskStatus status;

    public UpcomingTaskDTOBuilder taskId(UUID taskId) {
        this.taskId = taskId;
        return this;
    }

    public UpcomingTaskDTOBuilder taskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public UpcomingTaskDTOBuilder deadline(Instant deadline) {
        this.deadline = deadline;
        return this;
    }

    public UpcomingTaskDTOBuilder urgency(TaskUrgency urgency) {
        this.urgency = urgency;
        return this;
    }

    public UpcomingTaskDTOBuilder sourceType(String sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public UpcomingTaskDTOBuilder sourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    public UpcomingTaskDTOBuilder status(TaskStatus status) {
        this.status = status;
        return this;
    }

    public UpcomingTaskDTO build() {
        return new UpcomingTaskDTO(taskId, taskName, deadline, urgency, sourceType, sourceName, status);
    }
}

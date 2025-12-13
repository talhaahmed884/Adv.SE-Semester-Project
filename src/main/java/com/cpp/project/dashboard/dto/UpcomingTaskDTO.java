package com.cpp.project.dashboard.dto;

import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.dashboard.entity.TaskUrgency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for Upcoming Tasks
 * Used by Impending Doom widget to show tasks due soon
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingTaskDTO {
    private UUID taskId;
    private String taskName;
    private Instant deadline;
    private TaskUrgency urgency;       // TODAY, TOMORROW, THIS_WEEK
    private String sourceType;         // "COURSE" or "TODO_LIST"
    private String sourceName;         // Name of the course or todo list
    private TaskStatus status;         // PENDING, IN_PROGRESS, COMPLETED

    // Builder Pattern
    public static UpcomingTaskDTOBuilder builder() {
        return new UpcomingTaskDTOBuilder();
    }
}

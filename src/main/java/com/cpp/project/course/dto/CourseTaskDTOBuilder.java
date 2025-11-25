package com.cpp.project.course.dto;

import com.cpp.project.common.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Builder Pattern for CourseTaskDTO
 */
public class CourseTaskDTOBuilder {
    private UUID id;
    private String name;
    private String description;
    private Date deadline;
    private int progress;
    private TaskStatus status;
    private UUID courseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CourseTaskDTOBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public CourseTaskDTOBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CourseTaskDTOBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CourseTaskDTOBuilder deadline(Date deadline) {
        this.deadline = deadline;
        return this;
    }

    public CourseTaskDTOBuilder progress(int progress) {
        this.progress = progress;
        return this;
    }

    public CourseTaskDTOBuilder status(TaskStatus status) {
        this.status = status;
        return this;
    }

    public CourseTaskDTOBuilder courseId(UUID courseId) {
        this.courseId = courseId;
        return this;
    }

    public CourseTaskDTOBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public CourseTaskDTOBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public CourseTaskDTO build() {
        return new CourseTaskDTO(id, name, description, deadline, progress, status, courseId, createdAt, updatedAt);
    }
}

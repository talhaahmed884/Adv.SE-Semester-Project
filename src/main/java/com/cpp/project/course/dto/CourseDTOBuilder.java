package com.cpp.project.course.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Builder Pattern for CourseDTO
 */
public class CourseDTOBuilder {
    private UUID id;
    private String code;
    private String name;
    private UUID userId;
    private int progress;
    private List<CourseTaskDTO> tasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CourseDTOBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public CourseDTOBuilder code(String code) {
        this.code = code;
        return this;
    }

    public CourseDTOBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CourseDTOBuilder userId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public CourseDTOBuilder progress(int progress) {
        this.progress = progress;
        return this;
    }

    public CourseDTOBuilder tasks(List<CourseTaskDTO> tasks) {
        this.tasks = tasks;
        return this;
    }

    public CourseDTOBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public CourseDTOBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public CourseDTO build() {
        return new CourseDTO(id, code, name, userId, progress, tasks, createdAt, updatedAt);
    }
}

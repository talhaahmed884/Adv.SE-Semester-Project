package com.cpp.project.course.dto;

import java.time.Instant;

/**
 * Request DTO for updating a task
 */
public class UpdateTaskRequestDTO {
    private String name;
    private Instant deadline;
    private String description;

    public UpdateTaskRequestDTO() {
    }

    public UpdateTaskRequestDTO(String name, Instant deadline, String description) {
        this.name = name;
        this.deadline = deadline;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public void setDeadline(Instant deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEmpty() {
        return (name == null || name.trim().isEmpty()) &&
                deadline == null &&
                (description == null || description.trim().isEmpty());
    }
}

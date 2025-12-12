package com.cpp.project.todolist.dto;

import java.time.Instant;

/**
 * Request DTO for updating a todo list task
 */
public class UpdateToDoListTaskRequestDTO {
    private String description;
    private Instant deadline;

    public UpdateToDoListTaskRequestDTO() {
    }

    public UpdateToDoListTaskRequestDTO(String description, Instant deadline) {
        this.description = description;
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public void setDeadline(Instant deadline) {
        this.deadline = deadline;
    }

    public boolean isEmpty() {
        return (description == null || description.trim().isEmpty()) &&
                deadline == null;
    }
}

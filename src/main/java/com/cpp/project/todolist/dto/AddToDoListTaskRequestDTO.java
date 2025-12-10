package com.cpp.project.todolist.dto;

import java.time.Instant;

/**
 * Request DTO for adding a task to a todo list
 */
public class AddToDoListTaskRequestDTO {
    private String description;
    private Instant deadline;

    public AddToDoListTaskRequestDTO() {
    }

    public AddToDoListTaskRequestDTO(String description, Instant deadline) {
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
        return (description == null || description.trim().isEmpty()) && deadline == null;
    }
}

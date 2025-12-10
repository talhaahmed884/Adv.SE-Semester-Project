package com.cpp.project.todolist.dto;

import java.time.Instant;

/**
 * Request DTO for adding a task to a todo list
 * Description is required, deadline is optional
 */
public class AddToDoListTaskRequestDTO {
    private String description;
    private Instant deadline; // Optional - can be null

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
        // Only description is required; deadline is optional
        return description == null || description.trim().isEmpty();
    }
}

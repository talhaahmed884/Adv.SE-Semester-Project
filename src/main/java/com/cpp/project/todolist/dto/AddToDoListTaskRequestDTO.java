package com.cpp.project.todolist.dto;

import java.util.Date;

/**
 * Request DTO for adding a task to a todo list
 */
public class AddToDoListTaskRequestDTO {
    private String description;
    private Date deadline;

    public AddToDoListTaskRequestDTO() {
    }

    public AddToDoListTaskRequestDTO(String description, Date deadline) {
        this.description = description;
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean isEmpty() {
        return (description == null || description.trim().isEmpty()) && deadline == null;
    }
}

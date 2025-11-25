package com.cpp.project.todolist.dto;

import com.cpp.project.common.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Builder Pattern for ToDoListTaskDTO
 */
public class ToDoListTaskDTOBuilder {
    private UUID id;
    private String description;
    private Date deadline;
    private TaskStatus status;
    private UUID todoListId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ToDoListTaskDTOBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public ToDoListTaskDTOBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ToDoListTaskDTOBuilder deadline(Date deadline) {
        this.deadline = deadline;
        return this;
    }

    public ToDoListTaskDTOBuilder status(TaskStatus status) {
        this.status = status;
        return this;
    }

    public ToDoListTaskDTOBuilder todoListId(UUID todoListId) {
        this.todoListId = todoListId;
        return this;
    }

    public ToDoListTaskDTOBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ToDoListTaskDTOBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public ToDoListTaskDTO build() {
        return new ToDoListTaskDTO(id, description, deadline, status, todoListId, createdAt, updatedAt);
    }
}

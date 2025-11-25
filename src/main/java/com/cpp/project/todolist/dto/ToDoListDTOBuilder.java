package com.cpp.project.todolist.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Builder Pattern for ToDoListDTO
 */
public class ToDoListDTOBuilder {
    private UUID id;
    private String name;
    private UUID userId;
    private List<ToDoListTaskDTO> tasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ToDoListDTOBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public ToDoListDTOBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ToDoListDTOBuilder userId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public ToDoListDTOBuilder tasks(List<ToDoListTaskDTO> tasks) {
        this.tasks = tasks;
        return this;
    }

    public ToDoListDTOBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ToDoListDTOBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public ToDoListDTO build() {
        return new ToDoListDTO(id, name, userId, tasks, createdAt, updatedAt);
    }
}

package com.cpp.project.todolist.dto;

import java.util.UUID;

/**
 * Request DTO for creating a todo list
 */
public class CreateToDoListRequestDTO {
    private String name;
    private UUID userId;

    public CreateToDoListRequestDTO() {
    }

    public CreateToDoListRequestDTO(String name, UUID userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isEmpty() {
        return (name == null || name.trim().isEmpty()) && userId == null;
    }
}

package com.cpp.project.todolist.dto;

/**
 * Request DTO for updating a todo list
 */
public class UpdateToDoListRequestDTO {
    private String name;

    public UpdateToDoListRequestDTO() {
    }

    public UpdateToDoListRequestDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEmpty() {
        return name == null || name.trim().isEmpty();
    }
}

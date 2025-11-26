package com.cpp.project.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for ToDoList
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToDoListDTO {
    private UUID id;
    private String name;
    private UUID userId;
    private List<ToDoListTaskDTO> tasks = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder Pattern
    public static ToDoListDTOBuilder builder() {
        return new ToDoListDTOBuilder();
    }
}

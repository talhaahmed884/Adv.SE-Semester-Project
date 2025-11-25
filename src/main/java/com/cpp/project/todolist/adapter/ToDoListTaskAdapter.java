package com.cpp.project.todolist.adapter;

import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.entity.ToDoListTask;

/**
 * Adapter Pattern - Converts between ToDoListTask entities and DTOs
 */
public class ToDoListTaskAdapter {
    /**
     * Convert ToDoListTask entity to DTO
     */
    public static ToDoListTaskDTO toDTO(ToDoListTask task) {
        if (task == null) {
            return null;
        }

        return ToDoListTaskDTO.builder()
                .id(task.getId())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .status(task.getStatus())
                .todoListId(task.getTodoList() != null ? task.getTodoList().getId() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}

package com.cpp.project.todolist.adapter;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.entity.ToDoList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter Pattern - Converts between ToDoList entities and DTOs
 */
public class ToDoListAdapter {
    /**
     * Convert ToDoList entity to DTO
     */
    public static ToDoListDTO toDTO(ToDoList todoList) {
        if (todoList == null) {
            return null;
        }

        List<ToDoListTaskDTO> taskDTOs = todoList.getTasks() != null
                ? todoList.getTasks().stream()
                .map(ToDoListTaskAdapter::toDTO)
                .collect(Collectors.toList())
                : null;

        return ToDoListDTO.builder()
                .id(todoList.getId())
                .name(todoList.getName())
                .userId(todoList.getUserId())
                .tasks(taskDTOs)
                .createdAt(todoList.getCreatedAt())
                .updatedAt(todoList.getUpdatedAt())
                .build();
    }

    /**
     * Convert ToDoList entity to DTO without tasks (for list views)
     */
    public static ToDoListDTO toDTOWithoutTasks(ToDoList todoList) {
        if (todoList == null) {
            return null;
        }

        return ToDoListDTO.builder()
                .id(todoList.getId())
                .name(todoList.getName())
                .userId(todoList.getUserId())
                .createdAt(todoList.getCreatedAt())
                .updatedAt(todoList.getUpdatedAt())
                .build();
    }
}

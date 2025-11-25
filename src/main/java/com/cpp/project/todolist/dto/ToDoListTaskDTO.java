package com.cpp.project.todolist.dto;

import com.cpp.project.common.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Data Transfer Object for ToDoListTask
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToDoListTaskDTO {
    private UUID id;
    private String description;
    private Date deadline;
    private TaskStatus status;
    private UUID todoListId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder Pattern
    public static ToDoListTaskDTOBuilder builder() {
        return new ToDoListTaskDTOBuilder();
    }
}

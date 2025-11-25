package com.cpp.project.todolist.entity;

import com.cpp.project.common.entity.TaskStatus;

import java.util.Date;
import java.util.UUID;

/**
 * Builder Pattern for ToDoListTask entity
 */
public class ToDoListTaskBuilder {
    private UUID todoListId;
    private String description;
    private Date deadline;
    private TaskStatus status = TaskStatus.PENDING;

    public ToDoListTaskBuilder todoListId(UUID todoListId) {
        this.todoListId = todoListId;
        return this;
    }

    public ToDoListTaskBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ToDoListTaskBuilder deadline(Date deadline) {
        this.deadline = deadline;
        return this;
    }

    public ToDoListTaskBuilder status(TaskStatus status) {
        this.status = status;
        return this;
    }

    public ToDoListTask build() {
        ToDoListTask task = new ToDoListTask();
        task.setTodoListId(todoListId);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setStatus(status != null ? status : TaskStatus.PENDING);
        return task;
    }
}

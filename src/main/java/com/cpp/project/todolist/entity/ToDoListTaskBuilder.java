package com.cpp.project.todolist.entity;

import com.cpp.project.common.entity.TaskStatus;

import java.util.Date;

/**
 * Builder Pattern for ToDoListTask entity
 */
public class ToDoListTaskBuilder {
    private String description;
    private Date deadline;
    private TaskStatus status = TaskStatus.PENDING;
    private ToDoList todoList;

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

    public ToDoListTaskBuilder todoList(ToDoList todoList) {
        this.todoList = todoList;
        return this;
    }

    public ToDoListTask build() {
        ToDoListTask task = new ToDoListTask();
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setStatus(status != null ? status : TaskStatus.PENDING);
        task.setTodoList(todoList);
        return task;
    }
}

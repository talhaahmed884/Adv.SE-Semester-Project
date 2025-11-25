package com.cpp.project.todolist.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builder Pattern for ToDoList entity
 */
public class ToDoListBuilder {
    private String name;
    private UUID userId;
    private List<ToDoListTask> tasks = new ArrayList<>();

    public ToDoListBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ToDoListBuilder userId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public ToDoListBuilder tasks(List<ToDoListTask> tasks) {
        this.tasks = tasks;
        return this;
    }

    public ToDoList build() {
        ToDoList todoList = new ToDoList();
        todoList.setName(name);
        todoList.setUserId(userId);
        todoList.setTasks(tasks != null ? tasks : new ArrayList<>());
        return todoList;
    }
}

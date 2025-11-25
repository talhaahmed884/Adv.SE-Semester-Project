package com.cpp.project.todolist.repository;

import com.cpp.project.todolist.entity.ToDoList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ToDoList entity
 * Repository Pattern - Abstracts data access layer
 */
public interface ToDoListRepository {
    /**
     * Save a todo list (insert or update)
     */
    ToDoList save(ToDoList todoList);

    /**
     * Find a todo list by ID
     */
    Optional<ToDoList> findById(UUID id);

    /**
     * Find all todo lists for a user
     */
    List<ToDoList> findByUserId(UUID userId);

    /**
     * Delete a todo list by ID
     */
    void deleteById(UUID id);

    /**
     * Find all todo lists
     */
    List<ToDoList> findAll();
}

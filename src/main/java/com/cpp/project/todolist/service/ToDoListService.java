package com.cpp.project.todolist.service;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for ToDoList operations
 * Facade Pattern - Simplifies complex business logic
 */
public interface ToDoListService {
    /**
     * Create a new todo list
     *
     * @param name   List name
     * @param userId User who owns the list
     * @return Created todo list DTO
     */
    ToDoListDTO createToDoList(String name, UUID userId);

    /**
     * Get a todo list by ID
     *
     * @param id Todo list ID
     * @return Todo list DTO
     */
    ToDoListDTO getToDoListById(UUID id);

    /**
     * Get all todo lists for a user
     *
     * @param userId User ID
     * @return List of todo list DTOs
     */
    List<ToDoListDTO> getToDoListsByUserId(UUID userId);

    /**
     * Update a todo list name
     *
     * @param id   Todo list ID
     * @param name New list name
     * @return Updated todo list DTO
     */
    ToDoListDTO updateToDoList(UUID id, String name);

    /**
     * Delete a todo list
     *
     * @param id Todo list ID
     */
    void deleteToDoList(UUID id);

    /**
     * Add a task to a todo list
     *
     * @param todoListId  Todo list ID
     * @param description Task description
     * @param deadline    Task deadline
     * @return Created task DTO
     */
    ToDoListTaskDTO addTaskToList(UUID todoListId, String description, Date deadline);

    /**
     * Mark a task as complete
     *
     * @param todoListId Todo list ID
     * @param taskId     Task ID
     * @return Updated task DTO
     */
    ToDoListTaskDTO markTaskComplete(UUID todoListId, UUID taskId);

    /**
     * Get aggregated deadlines from all tasks in a list
     * Useful for calendar view
     *
     * @param todoListId Todo list ID
     * @return List of deadlines
     */
    List<Date> getAggregatedDeadlines(UUID todoListId);
}

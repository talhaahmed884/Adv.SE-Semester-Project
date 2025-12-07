package com.cpp.project.ui.mediator;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.ui.core.ScreenMediator;

import java.util.List;
import java.util.UUID;

/**
 * Mediator interface for ToDoList screen-state interactions
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Coordinates communication between states
 * - Facade Pattern: Simplifies access to services and data
 */
public interface ToDoListMediator extends ScreenMediator {

    // ========== Facade: Data Access Methods ==========

    /**
     * Get all todo lists for current user
     *
     * @return Fresh list of todo lists from service
     */
    List<ToDoListDTO> getAllToDoLists();

    /**
     * Get a specific todo list by ID
     *
     * @param listId The list ID
     * @return Fresh todo list from service
     */
    ToDoListDTO getToDoListById(UUID listId);

    // ========== Mediator: Action Notification Methods ==========

    /**
     * Called when a new todo list is created
     * Mediator decides next state (usually return to list view with message)
     */
    void onListCreated();

    /**
     * Called when a task is added to a list
     * Mediator decides next state (usually refresh details view)
     *
     * @param listId The list that was modified
     */
    void onTaskAdded(UUID listId);

    /**
     * Called when a task is marked complete
     * Mediator decides next state (usually refresh details view)
     *
     * @param listId The list that was modified
     */
    void onTaskCompleted(UUID listId);

    /**
     * Called when user wants to view a specific list
     * Mediator transitions to details state
     *
     * @param listId The list to view
     */
    void onViewListDetails(UUID listId);

    /**
     * Called when user wants to return to list view
     * Mediator transitions to list view state
     */
    void onReturnToListView();

    /**
     * Called when user wants to add a new list
     * Mediator transitions to add list state
     */
    void onAddNewList();

    /**
     * Called when user wants to add a task to a list
     * Mediator transitions to add task state
     *
     * @param listId The list to add task to
     */
    void onAddTaskToList(UUID listId);
}

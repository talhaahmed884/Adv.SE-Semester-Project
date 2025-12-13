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

    /**
     * Called when user wants to edit a list
     * Mediator transitions to edit list state
     *
     * @param listId The list to edit
     */
    void onEditList(UUID listId);

    /**
     * Called when a list is updated
     * Mediator decides next state (usually refresh details view)
     *
     * @param listId The list that was updated
     */
    void onListUpdated(UUID listId);

    /**
     * Called when user wants to delete a list
     * Mediator transitions to delete confirmation state
     *
     * @param listId The list to delete
     */
    void onDeleteList(UUID listId);

    /**
     * Called when a list is deleted
     * Mediator decides next state (usually return to list view with message)
     */
    void onListDeleted();

    /**
     * Called when user wants to edit a task
     * Mediator transitions to edit task state
     *
     * @param listId The list containing the task
     * @param taskId The task to edit
     */
    void onEditTask(UUID listId, UUID taskId);

    /**
     * Called when a task is updated
     * Mediator decides next state (usually refresh details view)
     *
     * @param listId The list containing the task
     */
    void onTaskUpdated(UUID listId);

    /**
     * Called when user wants to delete a task
     * Mediator transitions to delete confirmation state
     *
     * @param listId The list containing the task
     * @param taskId The task to delete
     */
    void onDeleteTask(UUID listId, UUID taskId);

    /**
     * Called when a task is deleted
     * Mediator decides next state (usually refresh details view)
     *
     * @param listId The list that was modified
     */
    void onTaskDeleted(UUID listId);

    // ========== Task Details View: Navigation Methods ==========

    /**
     * Called when user wants to view task details
     * Mediator transitions to task details state
     *
     * @param listId The list containing the task
     * @param taskId The task to view
     */
    void onViewTaskDetails(UUID listId, UUID taskId);

    // ========== Task Details View: Context-Aware Entry Points ==========

    /**
     * Called when user wants to mark task complete from task details view
     * Mediator may transition or handle inline
     *
     * @param listId The list containing the task
     * @param taskId The task to mark complete
     */
    void onMarkCompleteFromTaskDetails(UUID listId, UUID taskId);

    /**
     * Called when user wants to edit a task from task details view
     * Mediator transitions to edit task state with return context
     *
     * @param listId The list containing the task
     * @param taskId The task to edit
     */
    void onEditTaskFromTaskDetails(UUID listId, UUID taskId);

    /**
     * Called when user wants to delete a task from task details view
     * Mediator transitions to delete task state with return context
     *
     * @param listId The list containing the task
     * @param taskId The task to delete
     */
    void onDeleteTaskFromTaskDetails(UUID listId, UUID taskId);

    // ========== Task Details View: Callback Methods ==========

    /**
     * Called when a task is marked complete from task details view
     * Mediator returns to task details view with success message
     *
     * @param listId The list containing the task
     * @param taskId The task that was marked complete
     */
    void onTaskCompletedReturnToTaskDetails(UUID listId, UUID taskId);

    /**
     * Called when a task is updated from task details view
     * Mediator returns to task details view with success message
     *
     * @param listId The list containing the task
     * @param taskId The task that was updated
     */
    void onTaskUpdatedReturnToTaskDetails(UUID listId, UUID taskId);
}

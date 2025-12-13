package com.cpp.project.ui.screen;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.mediator.ToDoListMediator;
import com.cpp.project.ui.state.todolist.*;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;
import java.util.UUID;

/**
 * ToDoList Management Screen implementing Mediator pattern
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Coordinates all state interactions and transitions
 * - Facade Pattern: Provides simple interface for states to access data
 * - Factory Method Pattern: Creates states through factory methods
 * - State Pattern: Delegates UI behavior to state objects
 * <p>
 * Responsibilities:
 * - Owns the data (fetches from service)
 * - Coordinates state transitions
 * - Provides data access to states
 * - Handles state action notifications
 */
public class ToDoListManagementScreen extends StatefulScreen implements ToDoListMediator {
    private final UserDTO currentUser;
    private final ToDoListService toDoListService;

    public ToDoListManagementScreen(Screen screen, UserDTO currentUser, ToDoListService toDoListService) {
        super(screen);
        this.currentUser = currentUser;
        this.toDoListService = toDoListService;
        // Start with list view
        this.currentState = createListViewState(null);
        this.currentState.onEnter();
    }

    // ========== Facade Pattern: Simplified Data Access ==========

    @Override
    public List<ToDoListDTO> getAllToDoLists() {
        // Always fetch fresh from service - no caching, no stale data
        return toDoListService.getToDoListsByUserId(currentUser.getId());
    }

    @Override
    public ToDoListDTO getToDoListById(UUID listId) {
        // Always fetch fresh from service
        return toDoListService.getToDoListById(listId);
    }

    // ========== Mediator Pattern: Action Handlers ==========

    @Override
    public void onListCreated() {
        // User created a list, return to list view with success message
        transitionTo(createListViewState("To-Do List created successfully!"));
    }

    @Override
    public void onTaskAdded(UUID listId) {
        // User added a task, refresh the details view with success message
        transitionTo(createListDetailsState(listId, "Task added successfully!"));
    }

    @Override
    public void onTaskCompleted(UUID listId) {
        // User marked task complete, refresh the details view with success message
        transitionTo(createListDetailsState(listId, "Task marked as complete!"));
    }

    @Override
    public void onViewListDetails(UUID listId) {
        // User wants to view a list's details
        transitionTo(createListDetailsState(listId, null));
    }

    @Override
    public void onReturnToListView() {
        // User pressed ESC, return to list view
        transitionTo(createListViewState(null));
    }

    @Override
    public void onAddNewList() {
        // User pressed F1, show add list form
        transitionTo(createAddListState());
    }

    @Override
    public void onAddTaskToList(UUID listId) {
        // User pressed F2, show add task form
        transitionTo(createAddTaskState(listId));
    }

    @Override
    public void onEditList(UUID listId) {
        // User wants to edit list, show edit form
        transitionTo(createEditListState(listId));
    }

    @Override
    public void onListUpdated(UUID listId) {
        // List was updated, refresh details view with success message
        transitionTo(createListDetailsState(listId, "To-Do List updated successfully!"));
    }

    @Override
    public void onDeleteList(UUID listId) {
        // User wants to delete list, show confirmation dialog
        transitionTo(createDeleteListState(listId));
    }

    @Override
    public void onListDeleted() {
        // List was deleted, return to list view with success message
        transitionTo(createListViewState("To-Do List deleted successfully!"));
    }

    @Override
    public void onEditTask(UUID listId, UUID taskId) {
        // User wants to edit task, show edit form
        transitionTo(createEditTaskState(listId, taskId));
    }

    @Override
    public void onTaskUpdated(UUID listId) {
        // Task was updated, refresh details view with success message
        transitionTo(createListDetailsState(listId, "Task updated successfully!"));
    }

    @Override
    public void onDeleteTask(UUID listId, UUID taskId) {
        // User wants to delete task, show confirmation dialog
        transitionTo(createDeleteTaskState(listId, taskId));
    }

    @Override
    public void onTaskDeleted(UUID listId) {
        // Task was deleted, refresh details view with success message
        transitionTo(createListDetailsState(listId, "Task deleted successfully!"));
    }

    // ========== ScreenMediator: Core Methods ==========

    @Override
    public void transitionTo(ScreenState newState) {
        transitionToState(newState);
    }

    @Override
    public void closeScreen() {
        close();
    }

    // ========== Task Details View: Navigation Methods ==========

    @Override
    public void onViewTaskDetails(UUID listId, UUID taskId) {
        // User wants to view task details
        transitionTo(createTaskDetailsState(listId, taskId, null));
    }

    // ========== Task Details View: Context-Aware Entry Points ==========

    @Override
    public void onMarkCompleteFromTaskDetails(UUID listId, UUID taskId) {
        // Mark complete is handled directly in TaskDetailsState
        // This method exists for consistency but may not be used
    }

    @Override
    public void onEditTaskFromTaskDetails(UUID listId, UUID taskId) {
        // User wants to edit task from task details view
        transitionTo(createEditTaskState(listId, taskId, true));
    }

    @Override
    public void onDeleteTaskFromTaskDetails(UUID listId, UUID taskId) {
        // User wants to delete task from task details view
        transitionTo(createDeleteTaskState(listId, taskId, true));
    }

    // ========== Task Details View: Callback Methods ==========

    @Override
    public void onTaskCompletedReturnToTaskDetails(UUID listId, UUID taskId) {
        // Task marked complete from task details view, return with success message
        transitionTo(createTaskDetailsState(listId, taskId, "Task status updated successfully!"));
    }

    @Override
    public void onTaskUpdatedReturnToTaskDetails(UUID listId, UUID taskId) {
        // Task updated from task details view, return with success message
        transitionTo(createTaskDetailsState(listId, taskId, "Task updated successfully!"));
    }

    // ========== Factory Method Pattern: State Creation ==========

    /**
     * Factory method to create list view state
     *
     * @param message Optional success message to display
     * @return New list view state
     */
    private ListViewState createListViewState(String message) {
        return new ListViewState(this, message);
    }

    /**
     * Factory method to create list details state
     *
     * @param listId  The list to display
     * @param message Optional success message to display
     * @return New list details state
     */
    private ListDetailsState createListDetailsState(UUID listId, String message) {
        return new ListDetailsState(this, toDoListService, listId, message);
    }

    /**
     * Factory method to create add list state
     *
     * @return New add list state
     */
    private AddListState createAddListState() {
        return new AddListState(this, currentUser, toDoListService);
    }

    /**
     * Factory method to create add task state
     *
     * @param listId The list to add task to
     * @return New add task state
     */
    private AddTaskState createAddTaskState(UUID listId) {
        return new AddTaskState(this, toDoListService, listId);
    }

    /**
     * Factory method to create edit list state
     *
     * @param listId The list to edit
     * @return New edit list state
     */
    private EditListState createEditListState(UUID listId) {
        return new EditListState(this, toDoListService, listId);
    }

    /**
     * Factory method to create delete list state
     *
     * @param listId The list to delete
     * @return New delete list confirmation state
     */
    private DeleteListState createDeleteListState(UUID listId) {
        return new DeleteListState(this, toDoListService, listId);
    }

    /**
     * Factory method to create task details state
     *
     * @param listId  The list containing the task
     * @param taskId  The task to display
     * @param message Optional success message to display
     * @return New task details state
     */
    private TaskDetailsState createTaskDetailsState(UUID listId, UUID taskId, String message) {
        return new TaskDetailsState(this, toDoListService, listId, taskId, message);
    }

    /**
     * Factory method to create edit task state
     *
     * @param listId The list containing the task
     * @param taskId The task to edit
     * @return New edit task state
     */
    private com.cpp.project.ui.state.todolist.EditTaskState createEditTaskState(UUID listId, UUID taskId) {
        return createEditTaskState(listId, taskId, false);
    }

    /**
     * Factory method to create edit task state with context
     *
     * @param listId          The list containing the task
     * @param taskId          The task to edit
     * @param fromTaskDetails Whether called from task details view
     * @return New edit task state
     */
    private com.cpp.project.ui.state.todolist.EditTaskState createEditTaskState(UUID listId, UUID taskId, boolean fromTaskDetails) {
        return new com.cpp.project.ui.state.todolist.EditTaskState(this, toDoListService, listId, taskId, fromTaskDetails);
    }

    /**
     * Factory method to create delete task state
     *
     * @param listId The list containing the task
     * @param taskId The task to delete
     * @return New delete task confirmation state
     */
    private com.cpp.project.ui.state.todolist.DeleteTaskState createDeleteTaskState(UUID listId, UUID taskId) {
        return createDeleteTaskState(listId, taskId, false);
    }

    /**
     * Factory method to create delete task state with context
     *
     * @param listId          The list containing the task
     * @param taskId          The task to delete
     * @param fromTaskDetails Whether called from task details view
     * @return New delete task confirmation state
     */
    private com.cpp.project.ui.state.todolist.DeleteTaskState createDeleteTaskState(UUID listId, UUID taskId, boolean fromTaskDetails) {
        return new com.cpp.project.ui.state.todolist.DeleteTaskState(this, toDoListService, listId, taskId, fromTaskDetails);
    }
}

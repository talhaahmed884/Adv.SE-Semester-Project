package com.cpp.project.ui.screen;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.mediator.ToDoListMediator;
import com.cpp.project.ui.state.todolist.AddListState;
import com.cpp.project.ui.state.todolist.AddTaskState;
import com.cpp.project.ui.state.todolist.ListDetailsState;
import com.cpp.project.ui.state.todolist.ListViewState;
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

    // ========== ScreenMediator: Core Methods ==========

    @Override
    public void transitionTo(ScreenState newState) {
        transitionToState(newState);
    }

    @Override
    public void closeScreen() {
        close();
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
}

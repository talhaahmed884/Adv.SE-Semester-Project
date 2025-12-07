package com.cpp.project.ui.screen;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.state.todolist.AddTaskState;
import com.cpp.project.ui.state.todolist.ListDetailsState;
import com.cpp.project.ui.state.todolist.ListViewState;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

/**
 * Refactored To-Do List Management Screen using design patterns:
 * - State Pattern: State classes in ui.state.todolist package with adapter pattern
 * - Adapter Pattern: Screen adapters handle state transitions and data reloading
 * - Component Pattern: Reusable UI components
 * - Strategy Pattern: Validation strategies
 */
public class ToDoListManagementScreen extends StatefulScreen {
    private final UserDTO currentUser;
    private final ToDoListService toDoListService;
    private List<ToDoListDTO> todoLists;

    public ToDoListManagementScreen(Screen screen, UserDTO currentUser, ToDoListService toDoListService) {
        super(screen);
        this.currentUser = currentUser;
        this.toDoListService = toDoListService;
        reloadToDoLists();
        this.currentState = createListViewState();
        // Call onEnter to set initial focus
        this.currentState.onEnter();
    }

    private void reloadToDoLists() {
        todoLists = toDoListService.getToDoListsByUserId(currentUser.getId());
    }

    private List<ToDoListDTO> getToDoLists() {
        return todoLists;
    }

    private ListViewState createListViewState() {
        return new ListViewStateAdapter();
    }

    /**
     * Adapter that handles state transitions with proper data reloading
     */
    private class ListViewStateAdapter extends ListViewState {
        public ListViewStateAdapter() {
            super(screen, currentUser, toDoListService, todoLists, ToDoListManagementScreen.this::close);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            ScreenState newState = super.handleInput(keyStroke);

            // If AddListState returns null, reload and create new ListViewState
            if (newState == null) {
                reloadToDoLists();
                ListViewStateAdapter newListView = new ListViewStateAdapter();
                newListView.setSuccessMessage("To-Do List created successfully!");
                return newListView;
            }

            // If transitioning to ListDetailsState, wrap it in adapter
            if (newState instanceof ListDetailsState detailsState && !(newState instanceof ListDetailsStateAdapter)) {
                return new ListDetailsStateAdapter(detailsState.getToDoList(), this);
            }

            return newState;
        }
    }

    /**
     * Adapter that handles list details state transitions with proper data reloading
     */
    private class ListDetailsStateAdapter extends ListDetailsState {
        public ListDetailsStateAdapter(ToDoListDTO todoList, ListViewState listViewState) {
            super(screen, currentUser, toDoListService, todoList, listViewState);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            // Intercept F2 to provide all dependencies to AddTaskState
            if (keyStroke.getKeyType() == KeyType.F2) {
                return new AddTaskState(
                        screen,
                        currentUser,
                        toDoListService,
                        getToDoList(),
                        this,
                        ToDoListManagementScreen.this::reloadToDoLists,
                        ToDoListManagementScreen.this::getToDoLists
                );
            }
            // Let parent handle other inputs
            return super.handleInput(keyStroke);
        }
    }
}

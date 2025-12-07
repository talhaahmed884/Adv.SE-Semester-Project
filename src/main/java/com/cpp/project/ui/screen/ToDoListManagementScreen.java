package com.cpp.project.ui.screen;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.state.todolist.ListViewState;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

/**
 * Refactored To-Do List Management Screen using design patterns:
 * - State Pattern: State classes in ui.state.todolist package
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
        this.currentState = new ListViewState(screen, currentUser, toDoListService, todoLists, this::close);
        // Call onEnter to set initial focus
        this.currentState.onEnter();
    }

    private void reloadToDoLists() {
        todoLists = toDoListService.getToDoListsByUserId(currentUser.getId());
    }
}

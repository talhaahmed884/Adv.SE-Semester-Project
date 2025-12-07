package com.cpp.project.ui.state.todolist;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.DateInput;
import com.cpp.project.ui.component.Form;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.util.Date;

/**
 * State 4: Add Task
 */
public class AddTaskState implements ScreenState {
    private final Screen screen;
    private final UserDTO currentUser;
    private final ToDoListService toDoListService;
    private final ToDoListDTO todoList;
    private final ListDetailsState previousState;
    private boolean taskAdded = false;

    private final Form form;
    private final FormField descriptionField;
    private final DateInput deadlineInput;
    private final MessagePanel messagePanel;

    public AddTaskState(Screen screen, UserDTO currentUser, ToDoListService toDoListService,
                        ToDoListDTO todoList, ListDetailsState previousState) {
        this.screen = screen;
        this.currentUser = currentUser;
        this.toDoListService = toDoListService;
        this.todoList = todoList;
        this.previousState = previousState;

        descriptionField = ComponentFactory.createTextField("Description");
        deadlineInput = ComponentFactory.createDateInput("Deadline (optional)");

        form = new Form()
                .addField(descriptionField)
                .addField(deadlineInput);

        messagePanel = new MessagePanel();
    }

    @Override
    public void onEnter() {
        form.setFocused(true);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screen.getTerminalSize();

        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== ADD NEW TASK ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Tab: Next field | Enter: Save | ESC: Cancel");

        form.render(graphics, 5, 5);

        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(5, 14, "Note: Deadline is optional");

        messagePanel.render(graphics, 5, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Reload will be handled by parent screen
            return previousState;
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            return handleSave();
        } else {
            form.handleInput(keyStroke);
            return this;
        }
    }

    private ScreenState handleSave() {
        String description = descriptionField.getValue().trim();

        String error = new RequiredFieldStrategy("Task description").validate(description);
        if (error != null) {
            messagePanel.setError(error);
            return this;
        }

        Date deadline = null;
        if (!deadlineInput.isEmpty()) {
            deadline = deadlineInput.getDate();
            if (deadline == null) {
                messagePanel.setError(deadlineInput.getErrorMessage());
                return this;
            }
        }

        try {
            toDoListService.addTaskToList(todoList.getId(), description, deadline);
            taskAdded = true;
            // Return to previous state, which will check the flag and refresh
            return previousState;
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    public boolean wasTaskAdded() {
        return taskAdded;
    }

    public ToDoListDTO getToDoList() {
        return todoList;
    }

    @Override
    public String getStateName() {
        return "AddTask";
    }
}

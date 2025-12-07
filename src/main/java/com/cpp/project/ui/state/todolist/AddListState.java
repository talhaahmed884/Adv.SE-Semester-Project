package com.cpp.project.ui.state.todolist;

import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.mediator.ToDoListMediator;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * State 2: Add To-Do List
 *
 * Responsibilities:
 * - Collect list name from user
 * - Create new list via service
 * - Notify mediator on success or cancellation
 */
public class AddListState implements ScreenState {
    private final ToDoListMediator mediator;
    private final UserDTO currentUser;
    private final ToDoListService toDoListService;

    private final FormField nameField;
    private final MessagePanel messagePanel;

    public AddListState(ToDoListMediator mediator, UserDTO currentUser, ToDoListService toDoListService) {
        this.mediator = mediator;
        this.currentUser = currentUser;
        this.toDoListService = toDoListService;

        this.nameField = ComponentFactory.createTextField("List Name");
        this.nameField.setFocused(true);
        this.messagePanel = new MessagePanel();
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== ADD NEW TO-DO LIST ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Enter: Save | ESC: Cancel");

        nameField.render(graphics, 5, 5);

        messagePanel.render(graphics, 5, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to list view
            mediator.onReturnToListView();
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            return handleSave();
        } else {
            nameField.handleInput(keyStroke);
            return this;
        }
    }

    private ScreenState handleSave() {
        String name = nameField.getValue().trim();

        String error = new RequiredFieldStrategy("List name").validate(name);
        if (error != null) {
            messagePanel.setError(error);
            return this;
        }

        try {
            toDoListService.createToDoList(name, currentUser.getId());
            // Notify mediator - it will transition to list view with success message
            mediator.onListCreated();
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "AddList";
    }
}

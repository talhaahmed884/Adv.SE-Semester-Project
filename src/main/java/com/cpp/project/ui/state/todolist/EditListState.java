package com.cpp.project.ui.state.todolist;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.mediator.ToDoListMediator;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.UUID;

/**
 * State: Edit To-Do List
 * <p>
 * Responsibilities:
 * - Pre-fill form with current list data
 * - Collect updated list name from user
 * - Update list via service
 * - Notify mediator on success or cancellation
 */
public class EditListState implements ScreenState {
    private final ToDoListMediator mediator;
    private final ToDoListService toDoListService;
    private final UUID listId;
    private final FormField nameField;
    private final MessagePanel messagePanel;
    private ToDoListDTO todoList; // Cached list data

    public EditListState(ToDoListMediator mediator, ToDoListService toDoListService, UUID listId) {
        this.mediator = mediator;
        this.toDoListService = toDoListService;
        this.listId = listId;

        this.nameField = ComponentFactory.createTextField("List Name");
        this.nameField.setFocused(true);
        this.messagePanel = new MessagePanel();
    }

    @Override
    public void onEnter() {
        // Fetch fresh list data and pre-fill form
        todoList = mediator.getToDoListById(listId);
        nameField.setValue(todoList.getName());
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== EDIT TO-DO LIST ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Enter: Save | ESC: Cancel");

        // Name field
        nameField.render(graphics, 5, 5);

        // Messages
        messagePanel.render(graphics, 5, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to list details
            mediator.onViewListDetails(listId);
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

        // Validation
        String error = new RequiredFieldStrategy("List name").validate(name);
        if (error != null) {
            messagePanel.setError(error);
            return this;
        }

        // Check if anything changed
        if (name.equals(todoList.getName())) {
            messagePanel.setError("No changes detected");
            return this;
        }

        try {
            toDoListService.updateToDoList(listId, name);
            // Notify mediator - it will transition to list details with success message
            mediator.onListUpdated(listId);
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "EditList";
    }
}

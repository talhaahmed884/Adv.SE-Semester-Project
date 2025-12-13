package com.cpp.project.ui.state.todolist;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.mediator.ToDoListMediator;
import com.cpp.project.ui.util.FormValidator;
import com.cpp.project.ui.util.UILayoutConstants;
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
    private final FormValidator validator;
    private ToDoListDTO todoList; // Cached list data

    public EditListState(ToDoListMediator mediator, ToDoListService toDoListService, UUID listId) {
        this.mediator = mediator;
        this.toDoListService = toDoListService;
        this.listId = listId;

        this.nameField = ComponentFactory.createTextField("List Name");
        this.nameField.setFocused(true);
        this.messagePanel = new MessagePanel();
        this.validator = new FormValidator(messagePanel);
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
        graphics.putString((size.getColumns() - title.length()) / 2, UILayoutConstants.TITLE_ROW, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW, "Enter: Save | ESC: Cancel");

        // Name field
        nameField.render(graphics, UILayoutConstants.FORM_LEFT_MARGIN, UILayoutConstants.CONTENT_START_ROW);

        // Messages
        messagePanel.render(graphics, UILayoutConstants.FORM_LEFT_MARGIN, size.getRows() - UILayoutConstants.BOTTOM_MARGIN);
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
        if (!validator.validateRequired("List name", name)) {
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

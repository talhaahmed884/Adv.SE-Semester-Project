package com.cpp.project.ui.state.todolist;

import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.DateInput;
import com.cpp.project.ui.component.Form;
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

import java.time.Instant;
import java.util.UUID;

/**
 * State 4: Add Task
 * Responsibilities:
 * - Collect task details from user
 * - Add task to list via service
 * - Notify mediator on success or cancellation
 */
public class AddTaskState implements ScreenState {
    private final ToDoListMediator mediator;
    private final ToDoListService toDoListService;
    private final UUID listId;

    private final Form form;
    private final FormField descriptionField;
    private final DateInput deadlineInput;
    private final MessagePanel messagePanel;
    private final FormValidator validator;

    public AddTaskState(ToDoListMediator mediator, ToDoListService toDoListService, UUID listId) {
        this.mediator = mediator;
        this.toDoListService = toDoListService;
        this.listId = listId;

        descriptionField = ComponentFactory.createTextField("Description");
        deadlineInput = ComponentFactory.createDateInput("Deadline (optional)");

        form = new Form()
                .addField(descriptionField)
                .addField(deadlineInput);

        messagePanel = new MessagePanel();
        validator = new FormValidator(messagePanel);
    }

    @Override
    public void onEnter() {
        form.setFocused(true);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== ADD NEW TASK ===";
        graphics.putString(UILayoutConstants.centerX(size, title.length()), UILayoutConstants.TITLE_ROW, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW,
                "Tab: Next field | Enter: Save | ESC: Cancel");

        // Form
        form.render(graphics, UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW);

        // Note
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.NOTE_ROW, "Note: Deadline is optional");

        // Messages
        messagePanel.render(graphics, UILayoutConstants.FORM_LEFT, UILayoutConstants.messageRow(size));
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to details view
            mediator.onViewListDetails(listId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            return handleSave();
        } else {
            form.handleInput(keyStroke);
            return this;
        }
    }

    private ScreenState handleSave() {
        String description = descriptionField.getValue().trim();

        // Validate using FormValidator
        if (!validator.validateRequired("Task description", description)) {
            return this;
        }

        // Validate optional date input
        if (!validator.validateOptionalDateInput(deadlineInput)) {
            return this;
        }

        Instant deadline = deadlineInput.isEmpty() ? null : deadlineInput.getDate();

        try {
            toDoListService.addTaskToList(listId, description, deadline);
            // Notify mediator - it will transition to details view with success message
            mediator.onTaskAdded(listId);
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "AddTask";
    }
}

package com.cpp.project.ui.state.todolist;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
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
 * State: Edit Task
 * Responsibilities:
 * - Pre-fill form with current task data
 * - Collect updated task details from user
 * - Update task via service
 * - Notify mediator on success or cancellation
 */
public class EditTaskState implements ScreenState {
    private final ToDoListMediator mediator;
    private final ToDoListService toDoListService;
    private final UUID listId;
    private final UUID taskId;
    private final boolean fromTaskDetails;

    private final Form form;
    private final FormField descriptionField;
    private final DateInput deadlineInput;
    private final MessagePanel messagePanel;
    private final FormValidator validator;
    private ToDoListTaskDTO task; // Cached task data

    public EditTaskState(ToDoListMediator mediator, ToDoListService toDoListService, UUID listId, UUID taskId, boolean fromTaskDetails) {
        this.mediator = mediator;
        this.toDoListService = toDoListService;
        this.listId = listId;
        this.taskId = taskId;
        this.fromTaskDetails = fromTaskDetails;

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
        // Fetch fresh task data and pre-fill form
        ToDoListDTO todoList = mediator.getToDoListById(listId);
        task = todoList.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElse(null);

        if (task != null) {
            descriptionField.setValue(task.getDescription());
            if (task.getDeadline() != null) {
                deadlineInput.setDate(task.getDeadline());
            }
        }

        form.setFocused(true);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== EDIT TASK ===";
        graphics.putString((size.getColumns() - title.length()) / 2, UILayoutConstants.TITLE_ROW, title);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW, "Tab: Next field | Enter: Save | ESC: Cancel");

        form.render(graphics, UILayoutConstants.FORM_LEFT_MARGIN, UILayoutConstants.CONTENT_START_ROW);

        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(UILayoutConstants.FORM_LEFT_MARGIN, 14, "Note: Deadline is optional");

        messagePanel.render(graphics, UILayoutConstants.FORM_LEFT_MARGIN, size.getRows() - UILayoutConstants.BOTTOM_MARGIN);
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

        if (!validator.validateRequired("Task description", description)) {
            return this;
        }

        Instant deadline = null;
        if (!deadlineInput.isEmpty()) {
            deadline = deadlineInput.getDate();
            if (deadline == null) {
                messagePanel.setError(deadlineInput.getErrorMessage());
                return this;
            }
        }

        // Check if anything changed
        boolean descriptionChanged = !description.equals(task.getDescription());
        boolean deadlineChanged = (deadline == null && task.getDeadline() != null) ||
                (deadline != null && !deadline.equals(task.getDeadline()));

        if (!descriptionChanged && !deadlineChanged) {
            messagePanel.setError("No changes detected");
            return this;
        }

        try {
            toDoListService.updateTask(listId, taskId, description, deadline);
            // Notify mediator - it will transition based on context
            if (fromTaskDetails) {
                mediator.onTaskUpdatedReturnToTaskDetails(listId, taskId);
            } else {
                mediator.onTaskUpdated(listId);
            }
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "EditTask";
    }
}

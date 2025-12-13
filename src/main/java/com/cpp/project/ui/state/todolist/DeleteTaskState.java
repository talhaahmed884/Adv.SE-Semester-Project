package com.cpp.project.ui.state.todolist;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.ToDoListMediator;
import com.cpp.project.ui.util.DateFormatUtils;
import com.cpp.project.ui.util.UILayoutConstants;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.Arrays;
import java.util.UUID;

/**
 * State: Delete Task Confirmation
 * <p>
 * Responsibilities:
 * - Display task information
 * - Show warning and confirmation options
 * - Delete task via service if confirmed
 * - Notify mediator on success or cancellation
 */
public class DeleteTaskState implements ScreenState {
    private static final String CONFIRM = "Yes, Delete Task";
    private static final String CANCEL = "No, Cancel";
    private final ToDoListMediator mediator;
    private final ToDoListService toDoListService;
    private final UUID listId;
    private final UUID taskId;
    private final boolean fromTaskDetails;
    private final SelectionList<String> optionsList;
    private final MessagePanel messagePanel;
    private ToDoListTaskDTO task; // Cached task data

    public DeleteTaskState(ToDoListMediator mediator, ToDoListService toDoListService, UUID listId, UUID taskId, boolean fromTaskDetails) {
        this.mediator = mediator;
        this.toDoListService = toDoListService;
        this.listId = listId;
        this.taskId = taskId;
        this.fromTaskDetails = fromTaskDetails;

        // Create options list with Cancel as default (first item)
        this.optionsList = new SelectionList<>("Select an option", option -> option);
        this.optionsList.setItems(Arrays.asList(CANCEL, CONFIRM));
        this.optionsList.setFocused(true);

        this.messagePanel = new MessagePanel();
    }

    @Override
    public void onEnter() {
        // Fetch fresh task data
        ToDoListDTO todoList = mediator.getToDoListById(listId);
        task = todoList.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
        String title = "=== DELETE TASK - CONFIRMATION ===";
        graphics.putString((size.getColumns() - title.length()) / 2, UILayoutConstants.TITLE_ROW, title);

        // Warning
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW, "WARNING: This action cannot be undone!");

        if (task != null) {
            // Task info
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.CONTENT_START_ROW, "Description: " + task.getDescription());
            String deadlineText = task.getDeadline() != null ? DateFormatUtils.formatDeadline(task.getDeadline()) : "None";
            graphics.putString(UILayoutConstants.LEFT_MARGIN, 6, "Deadline: " + deadlineText);
            graphics.putString(UILayoutConstants.LEFT_MARGIN, 7, "Status: " + task.getStatus());
        }

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, 9, "Are you sure you want to delete this task?");

        // Options
        optionsList.render(graphics, UILayoutConstants.LEFT_MARGIN, 11);

        // Messages
        messagePanel.render(graphics, UILayoutConstants.LEFT_MARGIN, size.getRows() - UILayoutConstants.BOTTOM_MARGIN);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to list details
            mediator.onViewListDetails(listId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            return handleConfirmation();
        } else {
            optionsList.handleInput(keyStroke);
            return this;
        }
    }

    private ScreenState handleConfirmation() {
        String selected = optionsList.getSelectedItem();

        if (CANCEL.equals(selected)) {
            // User cancelled, return to list details
            mediator.onViewListDetails(listId);
            return null; // Mediator handles transition
        } else if (CONFIRM.equals(selected)) {
            // User confirmed, delete the task
            try {
                toDoListService.deleteTask(listId, taskId);
                // Notify mediator - it will transition to list details with success message
                mediator.onTaskDeleted(listId);
                return null; // Mediator handles transition
            } catch (Exception e) {
                messagePanel.setError(e.getMessage());
                return this;
            }
        }

        return this;
    }

    @Override
    public String getStateName() {
        return "DeleteTask";
    }
}

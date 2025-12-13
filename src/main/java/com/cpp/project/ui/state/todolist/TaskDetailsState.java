package com.cpp.project.ui.state.todolist;

import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.ToDoListMediator;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * State for viewing detailed information about a todo list task
 * <p>
 * Responsibilities:
 * - Display task details (description, deadline, status, timestamps)
 * - Provide access to task operations (mark complete, edit, delete)
 * - No data ownership - fetches fresh from mediator
 */
public class TaskDetailsState implements ScreenState {
    private final ToDoListMediator mediator;
    private final ToDoListService toDoListService;
    private final UUID listId;
    private final UUID taskId;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")
            .withZone(ZoneId.systemDefault());
    private final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");

    private final MessagePanel messagePanel;
    private ToDoListTaskDTO task;

    public TaskDetailsState(ToDoListMediator mediator, ToDoListService toDoListService,
                            UUID listId, UUID taskId, String successMessage) {
        this.mediator = mediator;
        this.toDoListService = toDoListService;
        this.listId = listId;
        this.taskId = taskId;

        this.messagePanel = new MessagePanel();
        if (successMessage != null) {
            messagePanel.setSuccess(successMessage);
        }
    }

    @Override
    public void onEnter() {
        // Fetch fresh list data
        ToDoListDTO list = mediator.getToDoListById(listId);

        // Extract the task from the list
        if (list.getTasks() != null) {
            task = list.getTasks().stream()
                    .filter(t -> t.getId().equals(taskId))
                    .findFirst()
                    .orElse(null);
        }

        // Handle task not found (may have been deleted)
        if (task == null) {
            messagePanel.setError("Task not found. Returning to list details...");
            // Return to list details after a brief moment
            mediator.onViewListDetails(listId);
        }
    }

    @Override
    public void render(TextGraphics graphics) {
        if (task == null) {
            // Task not found - show error
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(3, 3, "Task not found. Press ESC to return.");
            return;
        }

        TerminalSize size = graphics.getSize();

        // Title with task description
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String description = task.getDescription();
        if (description.length() > 50) {
            description = description.substring(0, 47) + "...";
        }
        String title = "=== TASK DETAILS: " + description + " ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        String instruction = TaskStatus.COMPLETED.equals(task.getStatus()) ?
                "F3: Mark Incomplete | F4: Edit | F5: Delete | ESC: Back" :
                "F3: Mark Complete | F4: Edit | F5: Delete | ESC: Back";
        graphics.putString(3, 3, instruction);

        // Section: Task Information
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(3, 5, "Task Information");
        graphics.putString(3, 6, "----------------------------------------");

        // Task details
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        int currentY = 7;

        // Description (full, with wrapping if needed)
        String fullDescription = task.getDescription();
        graphics.putString(5, currentY++, "Description: " + wrapText(fullDescription, 65));

        // Deadline
        String deadlineStr = task.getDeadline() != null ?
                dateFormat.format(task.getDeadline()) : "No deadline";
        graphics.putString(5, currentY++, "Deadline: " + deadlineStr);

        // Status with color
        TextColor statusColor = TaskStatus.COMPLETED.equals(task.getStatus()) ?
                TextColor.ANSI.GREEN : TextColor.ANSI.YELLOW;
        graphics.setForegroundColor(statusColor);
        graphics.putString(5, currentY++, "Status: " + task.getStatus());
        graphics.setForegroundColor(TextColor.ANSI.WHITE);

        // Timestamps
        currentY++;
        if (task.getCreatedAt() != null) {
            graphics.putString(5, currentY++, "Created: " + timestampFormat.format(task.getCreatedAt()));
        }
        if (task.getUpdatedAt() != null) {
            graphics.putString(5, currentY++, "Updated: " + timestampFormat.format(task.getUpdatedAt()));
        }

        // Messages at bottom
        messagePanel.render(graphics, 3, size.getRows() - 2);
    }

    /**
     * Wraps text to fit within specified width
     */
    private String wrapText(String text, int maxWidth) {
        if (text.length() <= maxWidth) {
            return text;
        }
        return text.substring(0, maxWidth - 3) + "...";
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (task == null) {
            // Task not found - only allow ESC
            if (keyStroke.getKeyType() == KeyType.Escape) {
                mediator.onViewListDetails(listId);
                return null;
            }
            return this;
        }

        if (keyStroke.getKeyType() == KeyType.F3) {
            // Mark complete/incomplete (toggle)
            return handleMarkComplete();
        } else if (keyStroke.getKeyType() == KeyType.F4) {
            // Edit task
            mediator.onEditTaskFromTaskDetails(listId, taskId);
            return null;
        } else if (keyStroke.getKeyType() == KeyType.F5) {
            // Delete task
            mediator.onDeleteTaskFromTaskDetails(listId, taskId);
            return null;
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            // Return to list details
            mediator.onViewListDetails(listId);
            return null;
        } else {
            return this;
        }
    }

    /**
     * Handle mark complete/incomplete toggle
     */
    private ScreenState handleMarkComplete() {
        if (task == null) {
            return this;
        }

        try {
            if (TaskStatus.COMPLETED.equals(task.getStatus())) {
                // Unmark complete (set to PENDING)
                // Note: Check if unmarkTaskComplete method exists in service
                // If not, we'll need to handle it differently
                toDoListService.markTaskInComplete(listId, taskId);
                mediator.onTaskCompletedReturnToTaskDetails(listId, taskId);
            } else {
                // Mark complete
                toDoListService.markTaskComplete(listId, taskId);
                mediator.onTaskCompletedReturnToTaskDetails(listId, taskId);
            }
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "TaskDetails";
    }
}

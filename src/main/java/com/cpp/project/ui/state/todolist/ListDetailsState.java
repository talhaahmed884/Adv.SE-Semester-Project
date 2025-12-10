package com.cpp.project.ui.state.todolist;

import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
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
 * State 3: List Details View
 * <p>
 * Responsibilities:
 * - Display details of a specific to-do list
 * - Handle adding tasks and marking complete
 * - No data ownership - fetches fresh from mediator
 */
public class ListDetailsState implements ScreenState {
    private final ToDoListMediator mediator;
    private final ToDoListService toDoListService;
    private final UUID listId;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            .withZone(ZoneId.systemDefault());

    private final SelectionList<ToDoListTaskDTO> taskSelection;
    private final MessagePanel messagePanel;
    private ToDoListDTO todoList; // Cached during render cycle

    public ListDetailsState(ToDoListMediator mediator, UUID listId, String successMessage) {
        this.mediator = mediator;
        this.toDoListService = null; // Will use mediator for data access
        this.listId = listId;

        this.taskSelection = new SelectionList<>("Tasks", task -> {
            String deadline = task.getDeadline() != null ?
                    dateFormat.format(task.getDeadline()) : "No deadline";
            return String.format("%s - %s (%s)", task.getDescription(), task.getStatus(), deadline);
        });
        taskSelection.setFocused(true);

        messagePanel = new MessagePanel();
        if (successMessage != null) {
            messagePanel.setSuccess(successMessage);
        }
    }

    // Constructor for backward compatibility with service injection
    public ListDetailsState(ToDoListMediator mediator, ToDoListService toDoListService,
                            UUID listId, String successMessage) {
        this.mediator = mediator;
        this.toDoListService = toDoListService;
        this.listId = listId;

        this.taskSelection = new SelectionList<>("Tasks", task -> {
            String deadline = task.getDeadline() != null ?
                    dateFormat.format(task.getDeadline()) : "No deadline";
            return String.format("%s - %s (%s)", task.getDescription(), task.getStatus(), deadline);
        });
        taskSelection.setFocused(true);

        messagePanel = new MessagePanel();
        if (successMessage != null) {
            messagePanel.setSuccess(successMessage);
        }
    }

    @Override
    public void onEnter() {
        // Fetch fresh data when entering this state
        todoList = mediator.getToDoListById(listId);
        if (todoList.getTasks() != null) {
            taskSelection.setItems(todoList.getTasks());
        }
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== TO-DO LIST DETAILS ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F2: Add Task | F3: Mark Complete | ESC: Back");

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        int completed = (int) todoList.getTasks().stream()
                .filter(t -> TaskStatus.COMPLETED.equals(t.getStatus()))
                .count();
        String listTitle = todoList.getName() + " (" + completed + "/" + todoList.getTasks().size() + " completed)";
        graphics.putString(3, 5, "List: " + listTitle);

        taskSelection.render(graphics, 3, 7);

        messagePanel.render(graphics, 3, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.F2) {
            // Notify mediator to show add task form
            mediator.onAddTaskToList(listId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F3) {
            return handleMarkComplete();
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to list view
            mediator.onReturnToListView();
            return null; // Mediator handles transition
        } else {
            taskSelection.handleInput(keyStroke);
            return this;
        }
    }

    private ScreenState handleMarkComplete() {
        if (taskSelection.isEmpty()) {
            messagePanel.setError("No tasks to mark as complete");
            return this;
        }

        ToDoListTaskDTO task = taskSelection.getSelectedItem();
        if (TaskStatus.COMPLETED.equals(task.getStatus())) {
            messagePanel.setError("Task is already completed");
            return this;
        }

        try {
            // Use injected service if available, otherwise we need to add it to mediator
            if (toDoListService != null) {
                toDoListService.markTaskComplete(listId, task.getId());
            }
            // Notify mediator that task was completed - it will refresh the view
            mediator.onTaskCompleted(listId);
            return null; // Mediator handles transition
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "ListDetails";
    }
}

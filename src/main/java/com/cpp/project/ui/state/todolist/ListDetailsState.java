package com.cpp.project.ui.state.todolist;

import com.cpp.project.common.entity.TaskStatus;
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

    private final SelectionList<ToDoListTaskDTO> taskSelection;
    private final MessagePanel messagePanel;
    private ToDoListDTO todoList; // Cached during render cycle

    public ListDetailsState(ToDoListMediator mediator, UUID listId, String successMessage) {
        this.mediator = mediator;
        this.toDoListService = null; // Will use mediator for data access
        this.listId = listId;

        this.taskSelection = new SelectionList<>("Tasks", task -> {
            String deadline = task.getDeadline() != null ?
                    DateFormatUtils.formatDeadline(task.getDeadline()) : "No deadline";
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
                    DateFormatUtils.formatDeadline(task.getDeadline()) : "No deadline";
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
        graphics.putString((size.getColumns() - title.length()) / 2, UILayoutConstants.TITLE_ROW, title);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW, "Enter: View Task Details | F2: Add Task | F4: Edit List | F5: Delete List | ESC: Back");

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        int completed = (int) todoList.getTasks().stream()
                .filter(t -> TaskStatus.COMPLETED.equals(t.getStatus()))
                .count();
        String listTitle = todoList.getName() + " (" + completed + "/" + todoList.getTasks().size() + " completed)";
        graphics.putString(UILayoutConstants.LEFT_MARGIN, 6, "List: " + listTitle);

        taskSelection.render(graphics, UILayoutConstants.LEFT_MARGIN, 8);

        messagePanel.render(graphics, UILayoutConstants.LEFT_MARGIN, size.getRows() - UILayoutConstants.BOTTOM_MARGIN);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Enter) {
            if (taskSelection.isEmpty()) {
                messagePanel.setError("No tasks available");
                return this;
            }
            // Notify mediator to show task details view
            ToDoListTaskDTO selectedTask = taskSelection.getSelectedItem();
            mediator.onViewTaskDetails(listId, selectedTask.getId());
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F2) {
            // Notify mediator to show add task form
            mediator.onAddTaskToList(listId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F4) {
            // Notify mediator to show edit list form
            mediator.onEditList(listId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F5) {
            // Notify mediator to show delete list confirmation
            mediator.onDeleteList(listId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to list view
            mediator.onReturnToListView();
            return null; // Mediator handles transition
        } else {
            taskSelection.handleInput(keyStroke);
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "ListDetails";
    }
}

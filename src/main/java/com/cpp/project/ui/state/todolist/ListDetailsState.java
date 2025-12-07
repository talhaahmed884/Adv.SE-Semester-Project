package com.cpp.project.ui.state.todolist;

import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.text.SimpleDateFormat;

/**
 * State 3: List Details View
 */
public class ListDetailsState implements ScreenState {
    private final Screen screen;
    private final UserDTO currentUser;
    private final ToDoListService toDoListService;
    private final ToDoListDTO todoList;
    private final ListViewState listViewState;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

    private final SelectionList<ToDoListTaskDTO> taskSelection;
    private final MessagePanel messagePanel;

    public ListDetailsState(Screen screen, UserDTO currentUser, ToDoListService toDoListService,
                            ToDoListDTO todoList, ListViewState listViewState) {
        this.screen = screen;
        this.currentUser = currentUser;
        this.toDoListService = toDoListService;
        this.todoList = todoList;
        this.listViewState = listViewState;

        this.taskSelection = new SelectionList<>("Tasks", task -> {
            String deadline = task.getDeadline() != null ?
                    dateFormat.format(task.getDeadline()) : "No deadline";
            return String.format("%s - %s (%s)", task.getDescription(), task.getStatus(), deadline);
        });

        if (todoList.getTasks() != null) {
            taskSelection.setItems(todoList.getTasks());
        }
        taskSelection.setFocused(true);
        messagePanel = new MessagePanel();
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screen.getTerminalSize();

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
            throw new UnsupportedOperationException("Subclass must handle F2 key");
        } else if (keyStroke.getKeyType() == KeyType.F3) {
            handleMarkComplete();
            return this;
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            // Adapter will reload data and create fresh list view
            return listViewState;
        } else {
            taskSelection.handleInput(keyStroke);
            return this;
        }
    }

    private void handleMarkComplete() {
        if (taskSelection.isEmpty()) {
            messagePanel.setError("No tasks to mark as complete");
            return;
        }

        ToDoListTaskDTO task = taskSelection.getSelectedItem();
        if (TaskStatus.COMPLETED.equals(task.getStatus())) {
            messagePanel.setError("Task is already completed");
            return;
        }

        try {
            toDoListService.markTaskComplete(todoList.getId(), task.getId());
            messagePanel.setSuccess("Task marked as complete!");
            // Reload will be handled by parent screen
        } catch (Exception e) {
            messagePanel.setError(e.getMessage());
        }
    }

    @Override
    public String getStateName() {
        return "ListDetails";
    }

    public void setSuccessMessage(String message) {
        messagePanel.setSuccess(message);
    }

    /**
     * Protected getter for todoList - allows adapter to access it
     */
    public ToDoListDTO getToDoList() {
        return todoList;
    }
}

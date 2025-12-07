package com.cpp.project.ui.screen;

import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.*;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.strategy.RequiredFieldStrategy;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Refactored To-Do List Management Screen using design patterns:
 * - State Pattern: Separate states for List, Add, Details, AddTask
 * - Component Pattern: Reusable UI components (eliminates code duplication with CourseManagement)
 * - Strategy Pattern: Validation strategies
 */
public class ToDoListManagementScreen extends StatefulScreen {
    private final UserDTO currentUser;
    private final ToDoListService toDoListService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private List<ToDoListDTO> todoLists;

    public ToDoListManagementScreen(Screen screen, UserDTO currentUser, ToDoListService toDoListService) {
        super(screen);
        this.currentUser = currentUser;
        this.toDoListService = toDoListService;
        loadToDoLists();
        this.currentState = new ListViewState();
    }

    private void loadToDoLists() {
        todoLists = toDoListService.getToDoListsByUserId(currentUser.getId());
    }

    /**
     * State 1: To-Do Lists View
     */
    private class ListViewState implements ScreenState {
        private final SelectionList<ToDoListDTO> listSelection;
        private final MessagePanel messagePanel;

        public ListViewState() {
            listSelection = new SelectionList<>("Your To-Do Lists", list -> {
                int completed = (int) list.getTasks().stream()
                        .filter(t -> TaskStatus.COMPLETED.equals(t.getStatus()))
                        .count();
                return list.getName() + " (" + completed + "/" + list.getTasks().size() + " completed)";
            });
            listSelection.setItems(todoLists);
            listSelection.setFocused(true);
            messagePanel = new MessagePanel();
        }

        @Override
        public void render(TextGraphics graphics) {
            TerminalSize size = screen.getTerminalSize();

            graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            String title = "=== TO-DO LIST MANAGEMENT ===";
            graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, 3, "F1: Add List | ESC: Back to Main Menu");

            listSelection.render(graphics, 3, 5);

            if (!listSelection.isEmpty()) {
                graphics.setForegroundColor(TextColor.ANSI.YELLOW);
                graphics.putString(3, 17, "Press ENTER to view list details");
            }

            messagePanel.render(graphics, 3, size.getRows() - 2);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            messagePanel.clear();

            if (keyStroke.getKeyType() == KeyType.F1) {
                return new AddListState(this);
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                close();
                return this;
            } else if (keyStroke.getKeyType() == KeyType.Enter && !listSelection.isEmpty()) {
                return new ListDetailsState(listSelection.getSelectedItem(), this);
            } else {
                listSelection.handleInput(keyStroke);
                return this;
            }
        }

        @Override
        public String getStateName() {
            return "ListView";
        }

        public void setSuccessMessage(String message) {
            messagePanel.setSuccess(message);
        }
    }

    /**
     * State 2: Add To-Do List
     */
    private class AddListState implements ScreenState {
        private final ListViewState previousState;
        private final FormField nameField;
        private final MessagePanel messagePanel;

        public AddListState(ListViewState previousState) {
            this.previousState = previousState;
            this.nameField = ComponentFactory.createTextField("List Name");
            this.nameField.setFocused(true);
            this.messagePanel = new MessagePanel();
        }

        @Override
        public void render(TextGraphics graphics) {
            TerminalSize size = screen.getTerminalSize();

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
                loadToDoLists();
                return previousState;
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
                loadToDoLists();
                previousState.setSuccessMessage("To-Do List created successfully!");
                return new ListViewState();
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

    /**
     * State 3: List Details View
     */
    private class ListDetailsState implements ScreenState {
        private final ToDoListDTO todoList;
        private final ListViewState listViewState;
        private final SelectionList<ToDoListTaskDTO> taskSelection;
        private final MessagePanel messagePanel;

        public ListDetailsState(ToDoListDTO todoList, ListViewState listViewState) {
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
                return new AddTaskState(todoList, this);
            } else if (keyStroke.getKeyType() == KeyType.F3) {
                handleMarkComplete();
                return this;
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                loadToDoLists();
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
                loadToDoLists();
                // Refresh the list
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
    }

    /**
     * State 4: Add Task
     */
    private class AddTaskState implements ScreenState {
        private final ToDoListDTO todoList;
        private final ListDetailsState previousState;
        private final Form form;
        private final FormField descriptionField;
        private final DateInput deadlineInput;
        private final MessagePanel messagePanel;

        public AddTaskState(ToDoListDTO todoList, ListDetailsState previousState) {
            this.todoList = todoList;
            this.previousState = previousState;

            descriptionField = ComponentFactory.createTextField("Description");
            deadlineInput = ComponentFactory.createDateInput("Deadline (optional)");

            form = new Form()
                    .addField(descriptionField)
                    .addField(deadlineInput);

            messagePanel = new MessagePanel();
        }

        @Override
        public void onEnter() {
            form.setFocused(true);
        }

        @Override
        public void render(TextGraphics graphics) {
            TerminalSize size = screen.getTerminalSize();

            graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            String title = "=== ADD NEW TASK ===";
            graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, 3, "Tab: Next field | Enter: Save | ESC: Cancel");

            form.render(graphics, 5, 5);

            graphics.setForegroundColor(TextColor.ANSI.CYAN);
            graphics.putString(5, 14, "Note: Deadline is optional");

            messagePanel.render(graphics, 5, size.getRows() - 2);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            messagePanel.clear();

            if (keyStroke.getKeyType() == KeyType.Escape) {
                loadToDoLists();
                return previousState;
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                return handleSave();
            } else {
                form.handleInput(keyStroke);
                return this;
            }
        }

        private ScreenState handleSave() {
            String description = descriptionField.getValue().trim();

            String error = new RequiredFieldStrategy("Task description").validate(description);
            if (error != null) {
                messagePanel.setError(error);
                return this;
            }

            Date deadline = null;
            if (!deadlineInput.isEmpty()) {
                deadline = deadlineInput.getDate();
                if (deadline == null) {
                    messagePanel.setError(deadlineInput.getErrorMessage());
                    return this;
                }
            }

            try {
                toDoListService.addTaskToList(todoList.getId(), description, deadline);
                loadToDoLists();
                ToDoListDTO updatedList = todoLists.stream()
                        .filter(l -> l.getId().equals(todoList.getId()))
                        .findFirst()
                        .orElse(todoList);
                ListDetailsState newDetailsState = new ListDetailsState(updatedList, new ListViewState());
                newDetailsState.setSuccessMessage("Task added successfully!");
                return newDetailsState;
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
}

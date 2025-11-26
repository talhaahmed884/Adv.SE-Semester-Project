package com.cpp.project.ui.screen;

import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * To-Do List management screen
 * Allows users to create lists, add tasks, and mark tasks as complete
 */
public class ToDoListManagementScreen {
    private final Screen screen;
    private final UserDTO currentUser;
    private final ToDoListService toDoListService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private List<ToDoListDTO> todoLists;
    private int selectedList = 0;
    private int selectedTask = 0;
    private int mode = 0; // 0=list view, 1=add list, 2=list details, 3=add task
    private String errorMessage = "";
    private String successMessage = "";
    // Input fields
    private String listName = "";
    private String taskDescription = "";
    private int taskDeadlineDay = 1;
    private int taskDeadlineMonth = 1;
    private int taskDeadlineYear = 2024;
    private int inputField = 0;

    public ToDoListManagementScreen(Screen screen, UserDTO currentUser, ToDoListService toDoListService) {
        this.screen = screen;
        this.currentUser = currentUser;
        this.toDoListService = toDoListService;
    }

    public void display() throws IOException {
        loadToDoLists();
        boolean running = true;

        while (running) {
            screen.clear();
            render();
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            if (keyStroke.getKeyType() == KeyType.Character) {
                handleCharacterInput(keyStroke.getCharacter());
            } else if (keyStroke.getKeyType() == KeyType.Backspace) {
                handleBackspace();
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                handleArrowUp();
            } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                handleArrowDown();
            } else if (keyStroke.getKeyType() == KeyType.Tab) {
                nextInputField();
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                handleEnter();
            } else if (keyStroke.getKeyType() == KeyType.F1) {
                mode = 1; // Add list
                clearInputs();
            } else if (keyStroke.getKeyType() == KeyType.F2 && mode == 2) {
                mode = 3; // Add task
                clearInputs();
            } else if (keyStroke.getKeyType() == KeyType.F3 && mode == 2) {
                handleMarkComplete();
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                if (mode == 0) {
                    running = false; // Back to main menu
                } else {
                    mode = 0; // Back to list view
                    loadToDoLists();
                    clearMessages();
                }
            }
        }
    }

    private void render() {
        TextGraphics graphics = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== TO-DO LIST MANAGEMENT ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        if (mode == 0) {
            renderListView(graphics);
        } else if (mode == 1) {
            renderAddList(graphics);
        } else if (mode == 2) {
            renderListDetails(graphics);
        } else if (mode == 3) {
            renderAddTask(graphics);
        }

        // Messages
        renderMessages(graphics, size.getRows() - 2);
    }

    private void renderListView(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F1: Add List | ESC: Back to Main Menu");

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(3, 5, "Your To-Do Lists:");

        if (todoLists.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(5, 7, "No lists yet. Press F1 to add one.");
        } else {
            int y = 7;
            for (int i = 0; i < todoLists.size(); i++) {
                ToDoListDTO list = todoLists.get(i);
                int completedTasks = (int) list.getTasks().stream()
                        .filter(t -> "COMPLETED".equals(t.getStatus()))
                        .count();
                String listLine = list.getName() + " (" + completedTasks + "/" + list.getTasks().size() + " completed)";

                if (i == selectedList) {
                    graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
                    graphics.putString(5, y, ">> " + listLine);
                } else {
                    graphics.setForegroundColor(TextColor.ANSI.WHITE);
                    graphics.putString(5, y, "   " + listLine);
                }
                y++;
            }
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, y + 1, "Press ENTER to view list details");
        }
    }

    private void renderAddList(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Add New To-Do List (Enter: Save, ESC: Cancel)");

        graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        graphics.putString(5, 5, "List Name: " + listName + "_");
    }

    private void renderListDetails(TextGraphics graphics) {
        if (todoLists.isEmpty()) {
            mode = 0;
            return;
        }

        ToDoListDTO list = todoLists.get(selectedList);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F2: Add Task | F3: Mark Complete | ESC: Back");

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(3, 5, "List: " + list.getName());

        graphics.putString(3, 7, "Tasks:");

        if (list.getTasks().isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(5, 9, "No tasks yet. Press F2 to add one.");
        } else {
            int y = 9;
            List<ToDoListTaskDTO> tasks = list.getTasks();
            for (int i = 0; i < tasks.size(); i++) {
                ToDoListTaskDTO task = tasks.get(i);
                String deadline = task.getDeadline() != null ? dateFormat.format(task.getDeadline()) : "No deadline";
                String taskLine = String.format("%s - %s (%s)",
                        task.getDescription(),
                        task.getStatus(),
                        deadline);

                if (i == selectedTask) {
                    graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
                    graphics.putString(5, y, ">> " + taskLine);
                } else {
                    graphics.setForegroundColor(TextColor.ANSI.WHITE);
                    graphics.putString(5, y, "   " + taskLine);
                }
                y++;
            }
        }
    }

    private void renderAddTask(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Add New Task (Tab: Next field, Enter: Save, ESC: Cancel)");

        int y = 5;
        graphics.setForegroundColor(inputField == 0 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, y, "Description: " + taskDescription + (inputField == 0 ? "_" : ""));

        y += 2;
        graphics.setForegroundColor(inputField == 1 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, y, "Deadline Year: " + taskDeadlineYear + (inputField == 1 ? "_" : ""));

        y += 1;
        graphics.setForegroundColor(inputField == 2 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, y, "Deadline Month (1-12): " + taskDeadlineMonth + (inputField == 2 ? "_" : ""));

        y += 1;
        graphics.setForegroundColor(inputField == 3 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, y, "Deadline Day (1-31): " + taskDeadlineDay + (inputField == 3 ? "_" : ""));

        y += 2;
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(5, y, "Note: Deadline is optional, will be set if valid date entered");
    }

    private void renderMessages(TextGraphics graphics, int y) {
        if (!errorMessage.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
            graphics.putString(3, y, "Error: " + errorMessage);
        }
        if (!successMessage.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(3, y, successMessage);
        }
    }

    private void handleCharacterInput(Character c) {
        clearMessages();

        if (mode == 1) { // Add list
            listName += c;
        } else if (mode == 3) { // Add task
            if (inputField == 0) taskDescription += c;
            else if (inputField == 1 && Character.isDigit(c))
                taskDeadlineYear = Integer.parseInt(taskDeadlineYear + "" + c);
            else if (inputField == 2 && Character.isDigit(c)) {
                int val = Integer.parseInt(taskDeadlineMonth + "" + c);
                if (val <= 12) taskDeadlineMonth = val;
            } else if (inputField == 3 && Character.isDigit(c)) {
                int val = Integer.parseInt(taskDeadlineDay + "" + c);
                if (val <= 31) taskDeadlineDay = val;
            }
        }
    }

    private void handleBackspace() {
        clearMessages();

        if (mode == 1 && !listName.isEmpty()) {
            listName = listName.substring(0, listName.length() - 1);
        } else if (mode == 3) {
            if (inputField == 0 && !taskDescription.isEmpty())
                taskDescription = taskDescription.substring(0, taskDescription.length() - 1);
            else if (inputField == 1) taskDeadlineYear = 2024;
            else if (inputField == 2) taskDeadlineMonth = 1;
            else if (inputField == 3) taskDeadlineDay = 1;
        }
    }

    private void handleArrowUp() {
        if (mode == 0 && !todoLists.isEmpty()) {
            selectedList = (selectedList - 1 + todoLists.size()) % todoLists.size();
        } else if (mode == 2 && !todoLists.isEmpty() && !todoLists.get(selectedList).getTasks().isEmpty()) {
            int taskCount = todoLists.get(selectedList).getTasks().size();
            selectedTask = (selectedTask - 1 + taskCount) % taskCount;
        }
    }

    private void handleArrowDown() {
        if (mode == 0 && !todoLists.isEmpty()) {
            selectedList = (selectedList + 1) % todoLists.size();
        } else if (mode == 2 && !todoLists.isEmpty() && !todoLists.get(selectedList).getTasks().isEmpty()) {
            int taskCount = todoLists.get(selectedList).getTasks().size();
            selectedTask = (selectedTask + 1) % taskCount;
        }
    }

    private void nextInputField() {
        if (mode == 3) {
            inputField = (inputField + 1) % 4;
        }
    }

    private void handleEnter() {
        clearMessages();

        try {
            if (mode == 0 && !todoLists.isEmpty()) {
                // View list details
                mode = 2;
                selectedTask = 0;
            } else if (mode == 1) {
                // Save new list
                if (listName.trim().isEmpty()) {
                    errorMessage = "List name is required";
                    return;
                }

                toDoListService.createToDoList(listName.trim(), currentUser.getId());
                successMessage = "To-Do List created successfully!";
                mode = 0;
                loadToDoLists();
                clearInputs();
            } else if (mode == 3) {
                // Save new task
                if (taskDescription.trim().isEmpty()) {
                    errorMessage = "Task description is required";
                    return;
                }

                ToDoListDTO list = todoLists.get(selectedList);
                Date deadline = null;

                // Try to create deadline if valid date is entered
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.set(taskDeadlineYear, taskDeadlineMonth - 1, taskDeadlineDay);
                    deadline = cal.getTime();
                } catch (Exception e) {
                    // Deadline remains null if date is invalid
                }

                toDoListService.addTaskToList(list.getId(), taskDescription.trim(), deadline);
                successMessage = "Task added successfully!";
                mode = 2;
                loadToDoLists();
                clearInputs();
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            if (errorMessage.length() > 50) {
                errorMessage = errorMessage.substring(0, 50) + "...";
            }
        }
    }

    private void handleMarkComplete() {
        clearMessages();

        try {
            if (todoLists.isEmpty()) return;

            ToDoListDTO list = todoLists.get(selectedList);
            if (list.getTasks().isEmpty()) {
                errorMessage = "No tasks to mark as complete";
                return;
            }

            ToDoListTaskDTO task = list.getTasks().get(selectedTask);
            if ("COMPLETED".equals(task.getStatus())) {
                errorMessage = "Task is already completed";
                return;
            }

            toDoListService.markTaskComplete(list.getId(), task.getId());
            successMessage = "Task marked as complete!";
            loadToDoLists();
        } catch (Exception e) {
            errorMessage = e.getMessage();
            if (errorMessage.length() > 50) {
                errorMessage = errorMessage.substring(0, 50) + "...";
            }
        }
    }

    private void loadToDoLists() {
        todoLists = toDoListService.getToDoListsByUserId(currentUser.getId());
        if (selectedList >= todoLists.size()) {
            selectedList = Math.max(0, todoLists.size() - 1);
        }
    }

    private void clearInputs() {
        listName = "";
        taskDescription = "";
        taskDeadlineYear = 2024;
        taskDeadlineMonth = 1;
        taskDeadlineDay = 1;
        inputField = 0;
    }

    private void clearMessages() {
        errorMessage = "";
        successMessage = "";
    }
}

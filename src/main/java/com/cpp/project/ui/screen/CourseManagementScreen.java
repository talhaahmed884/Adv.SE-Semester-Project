package com.cpp.project.ui.screen;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
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
 * Course management screen
 * Allows users to create courses, add tasks, and manage task progress
 */
public class CourseManagementScreen {
    private final Screen screen;
    private final UserDTO currentUser;
    private final CourseService courseService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private List<CourseDTO> courses;
    private int selectedCourse = 0;
    private int selectedTask = 0;
    private int mode = 0; // 0=course list, 1=add course, 2=course details, 3=add task, 4=update progress
    private String errorMessage = "";
    private String successMessage = "";
    // Input fields
    private String courseCode = "";
    private String courseName = "";
    private String taskName = "";
    private String taskDescription = "";
    private int taskDeadlineDay = 1;
    private int taskDeadlineMonth = 1;
    private int taskDeadlineYear = 2024;
    private String progressInput = "";
    private int inputField = 0;

    public CourseManagementScreen(Screen screen, UserDTO currentUser, CourseService courseService) {
        this.screen = screen;
        this.currentUser = currentUser;
        this.courseService = courseService;
    }

    public void display() throws IOException {
        loadCourses();
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
                mode = 1; // Add course
                clearInputs();
            } else if (keyStroke.getKeyType() == KeyType.F2 && mode == 2) {
                mode = 3; // Add task
                clearInputs();
            } else if (keyStroke.getKeyType() == KeyType.F3 && mode == 2) {
                mode = 4; // Update progress
                progressInput = "";
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                if (mode == 0) {
                    running = false; // Back to main menu
                } else {
                    mode = 0; // Back to course list
                    loadCourses();
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
        String title = "=== COURSE MANAGEMENT ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        if (mode == 0) {
            renderCourseList(graphics);
        } else if (mode == 1) {
            renderAddCourse(graphics);
        } else if (mode == 2) {
            renderCourseDetails(graphics);
        } else if (mode == 3) {
            renderAddTask(graphics);
        } else if (mode == 4) {
            renderUpdateProgress(graphics);
        }

        // Messages
        renderMessages(graphics, size.getRows() - 2);
    }

    private void renderCourseList(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F1: Add Course | ESC: Back to Main Menu");

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(3, 5, "Your Courses:");

        if (courses.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(5, 7, "No courses yet. Press F1 to add one.");
        } else {
            int y = 7;
            for (int i = 0; i < courses.size(); i++) {
                CourseDTO course = courses.get(i);
                if (i == selectedCourse) {
                    graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
                    graphics.putString(5, y, ">> " + course.getCode() + " - " + course.getName());
                } else {
                    graphics.setForegroundColor(TextColor.ANSI.WHITE);
                    graphics.putString(5, y, "   " + course.getCode() + " - " + course.getName());
                }
                y++;
            }
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, y + 1, "Press ENTER to view course details");
        }
    }

    private void renderAddCourse(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Add New Course (Tab: Next field, Enter: Save, ESC: Cancel)");

        int y = 5;
        graphics.setForegroundColor(inputField == 0 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, y, "Course Code: " + courseCode + (inputField == 0 ? "_" : ""));

        y += 2;
        graphics.setForegroundColor(inputField == 1 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, y, "Course Name: " + courseName + (inputField == 1 ? "_" : ""));
    }

    private void renderCourseDetails(TextGraphics graphics) {
        if (courses.isEmpty()) {
            mode = 0;
            return;
        }

        CourseDTO course = courses.get(selectedCourse);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F2: Add Task | F3: Update Progress | ESC: Back");

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(3, 5, "Course: " + course.getCode() + " - " + course.getName());

        graphics.putString(3, 7, "Tasks:");

        if (course.getTasks().isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(5, 9, "No tasks yet. Press F2 to add one.");
        } else {
            int y = 9;
            List<CourseTaskDTO> tasks = course.getTasks();
            for (int i = 0; i < tasks.size(); i++) {
                CourseTaskDTO task = tasks.get(i);
                String taskLine = String.format("%s - %s [%d%%] (%s)",
                        task.getName(),
                        task.getStatus(),
                        task.getProgress(),
                        dateFormat.format(task.getDeadline()));

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
        graphics.putString(5, y, "Task Name: " + taskName + (inputField == 0 ? "_" : ""));

        y += 2;
        graphics.setForegroundColor(inputField == 1 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, y, "Description: " + taskDescription + (inputField == 1 ? "_" : ""));

        y += 2;
        graphics.setForegroundColor(inputField == 2 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, y, "Deadline Year: " + taskDeadlineYear + (inputField == 2 ? "_" : ""));

        y += 1;
        graphics.setForegroundColor(inputField == 3 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, y, "Deadline Month (1-12): " + taskDeadlineMonth + (inputField == 3 ? "_" : ""));

        y += 1;
        graphics.setForegroundColor(inputField == 4 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE);
        graphics.putString(5, y, "Deadline Day (1-31): " + taskDeadlineDay + (inputField == 4 ? "_" : ""));
    }

    private void renderUpdateProgress(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Update Task Progress (Enter: Save, ESC: Cancel)");

        CourseDTO course = courses.get(selectedCourse);
        if (course.getTasks().isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(5, 5, "No tasks available");
            return;
        }

        CourseTaskDTO task = course.getTasks().get(selectedTask);

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(5, 5, "Task: " + task.getName());
        graphics.putString(5, 6, "Current Progress: " + task.getProgress() + "%");

        graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        graphics.putString(5, 8, "New Progress (0-100): " + progressInput + "_");
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

        if (mode == 1) { // Add course
            if (inputField == 0) courseCode += c;
            else if (inputField == 1) courseName += c;
        } else if (mode == 3) { // Add task
            if (inputField == 0) taskName += c;
            else if (inputField == 1) taskDescription += c;
            else if (inputField == 2 && Character.isDigit(c))
                taskDeadlineYear = Integer.parseInt(taskDeadlineYear + "" + c);
            else if (inputField == 3 && Character.isDigit(c)) {
                int val = Integer.parseInt(taskDeadlineMonth + "" + c);
                if (val <= 12) taskDeadlineMonth = val;
            } else if (inputField == 4 && Character.isDigit(c)) {
                int val = Integer.parseInt(taskDeadlineDay + "" + c);
                if (val <= 31) taskDeadlineDay = val;
            }
        } else if (mode == 4) { // Update progress
            if (Character.isDigit(c)) {
                progressInput += c;
            }
        }
    }

    private void handleBackspace() {
        clearMessages();

        if (mode == 1) {
            if (inputField == 0 && !courseCode.isEmpty())
                courseCode = courseCode.substring(0, courseCode.length() - 1);
            else if (inputField == 1 && !courseName.isEmpty())
                courseName = courseName.substring(0, courseName.length() - 1);
        } else if (mode == 3) {
            if (inputField == 0 && !taskName.isEmpty())
                taskName = taskName.substring(0, taskName.length() - 1);
            else if (inputField == 1 && !taskDescription.isEmpty())
                taskDescription = taskDescription.substring(0, taskDescription.length() - 1);
            else if (inputField == 2) taskDeadlineYear = 2024;
            else if (inputField == 3) taskDeadlineMonth = 1;
            else if (inputField == 4) taskDeadlineDay = 1;
        } else if (mode == 4 && !progressInput.isEmpty()) {
            progressInput = progressInput.substring(0, progressInput.length() - 1);
        }
    }

    private void handleArrowUp() {
        if (mode == 0 && !courses.isEmpty()) {
            selectedCourse = (selectedCourse - 1 + courses.size()) % courses.size();
        } else if (mode == 2 && !courses.isEmpty() && !courses.get(selectedCourse).getTasks().isEmpty()) {
            int taskCount = courses.get(selectedCourse).getTasks().size();
            selectedTask = (selectedTask - 1 + taskCount) % taskCount;
        }
    }

    private void handleArrowDown() {
        if (mode == 0 && !courses.isEmpty()) {
            selectedCourse = (selectedCourse + 1) % courses.size();
        } else if (mode == 2 && !courses.isEmpty() && !courses.get(selectedCourse).getTasks().isEmpty()) {
            int taskCount = courses.get(selectedCourse).getTasks().size();
            selectedTask = (selectedTask + 1) % taskCount;
        }
    }

    private void nextInputField() {
        if (mode == 1) {
            inputField = (inputField + 1) % 2;
        } else if (mode == 3) {
            inputField = (inputField + 1) % 5;
        }
    }

    private void handleEnter() {
        clearMessages();

        try {
            if (mode == 0 && !courses.isEmpty()) {
                // View course details
                mode = 2;
                selectedTask = 0;
            } else if (mode == 1) {
                // Save new course
                if (courseCode.trim().isEmpty() || courseName.trim().isEmpty()) {
                    errorMessage = "Course code and name are required";
                    return;
                }

                courseService.createCourse(courseCode.trim(), courseName.trim(), currentUser.getId());
                successMessage = "Course created successfully!";
                mode = 0;
                loadCourses();
                clearInputs();
            } else if (mode == 3) {
                // Save new task
                if (taskName.trim().isEmpty()) {
                    errorMessage = "Task name is required";
                    return;
                }

                CourseDTO course = courses.get(selectedCourse);
                Calendar cal = Calendar.getInstance();
                cal.set(taskDeadlineYear, taskDeadlineMonth - 1, taskDeadlineDay);
                Date deadline = cal.getTime();

                courseService.addTaskToCourse(course.getId(), taskName.trim(), deadline, taskDescription.trim());
                successMessage = "Task added successfully!";
                mode = 2;
                loadCourses();
                clearInputs();
            } else if (mode == 4) {
                // Update progress
                if (progressInput.trim().isEmpty()) {
                    errorMessage = "Progress value is required";
                    return;
                }

                int progress = Integer.parseInt(progressInput.trim());
                if (progress < 0 || progress > 100) {
                    errorMessage = "Progress must be between 0 and 100";
                    return;
                }

                CourseDTO course = courses.get(selectedCourse);
                CourseTaskDTO task = course.getTasks().get(selectedTask);

                courseService.updateTaskProgress(course.getId(), task.getId(), progress);

                if (progress == 100) {
                    courseService.markTaskComplete(course.getId(), task.getId());
                    successMessage = "Task marked as complete!";
                } else {
                    successMessage = "Progress updated successfully!";
                }

                mode = 2;
                loadCourses();
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            if (errorMessage.length() > 50) {
                errorMessage = errorMessage.substring(0, 50) + "...";
            }
        }
    }

    private void loadCourses() {
        courses = courseService.getCoursesByUserId(currentUser.getId());
        if (selectedCourse >= courses.size()) {
            selectedCourse = Math.max(0, courses.size() - 1);
        }
    }

    private void clearInputs() {
        courseCode = "";
        courseName = "";
        taskName = "";
        taskDescription = "";
        taskDeadlineYear = 2024;
        taskDeadlineMonth = 1;
        taskDeadlineDay = 1;
        progressInput = "";
        inputField = 0;
    }

    private void clearMessages() {
        errorMessage = "";
        successMessage = "";
    }
}

package com.cpp.project.ui.screen;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
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
 * Refactored Course Management Screen using design patterns:
 * - State Pattern: Separate state for each mode (List, Add, Details, AddTask, UpdateProgress)
 * - Component Pattern: Reusable UI components
 * - Strategy Pattern: Validation strategies
 * <p>
 * This replaces the 469-line god class with clean, maintainable states
 */
public class CourseManagementScreen extends StatefulScreen {
    private final UserDTO currentUser;
    private final CourseService courseService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private List<CourseDTO> courses;

    public CourseManagementScreen(Screen screen, UserDTO currentUser, CourseService courseService) {
        super(screen);
        this.currentUser = currentUser;
        this.courseService = courseService;
        loadCourses();
        this.currentState = new CourseListState();
    }

    private void loadCourses() {
        courses = courseService.getCoursesByUserId(currentUser.getId());
    }

    /**
     * State 1: Course List View
     */
    private class CourseListState implements ScreenState {
        private final SelectionList<CourseDTO> courseList;
        private final MessagePanel messagePanel;

        public CourseListState() {
            courseList = new SelectionList<>("Your Courses", course ->
                    course.getCode() + " - " + course.getName()
            );
            courseList.setItems(courses);
            courseList.setFocused(true);
            messagePanel = new MessagePanel();
        }

        @Override
        public void render(TextGraphics graphics) {
            TerminalSize size = screen.getTerminalSize();

            // Title
            graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            String title = "=== COURSE MANAGEMENT ===";
            graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

            // Instructions
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, 3, "F1: Add Course | ESC: Back to Main Menu");

            // Course list
            courseList.render(graphics, 3, 5);

            if (!courseList.isEmpty()) {
                graphics.setForegroundColor(TextColor.ANSI.YELLOW);
                graphics.putString(3, 17, "Press ENTER to view course details");
            }

            // Messages
            messagePanel.render(graphics, 3, size.getRows() - 2);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            messagePanel.clear();

            if (keyStroke.getKeyType() == KeyType.F1) {
                return new AddCourseState(this);
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                close();
                return this;
            } else if (keyStroke.getKeyType() == KeyType.Enter && !courseList.isEmpty()) {
                return new CourseDetailsState(courseList.getSelectedItem(), this);
            } else {
                courseList.handleInput(keyStroke);
                return this;
            }
        }

        @Override
        public String getStateName() {
            return "CourseList";
        }

        public void setSuccessMessage(String message) {
            messagePanel.setSuccess(message);
        }
    }

    /**
     * State 2: Add Course Form
     */
    private class AddCourseState implements ScreenState {
        private final CourseListState previousState;
        private final Form form;
        private final FormField codeField;
        private final FormField nameField;
        private final MessagePanel messagePanel;

        public AddCourseState(CourseListState previousState) {
            this.previousState = previousState;
            codeField = ComponentFactory.createTextField("Course Code");
            nameField = ComponentFactory.createTextField("Course Name");

            form = new Form()
                    .addField(codeField)
                    .addField(nameField);

            messagePanel = new MessagePanel();
        }

        @Override
        public void onEnter() {
            form.setFocused(true);
        }

        @Override
        public void render(TextGraphics graphics) {
            TerminalSize size = screen.getTerminalSize();

            // Title
            graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            String title = "=== ADD NEW COURSE ===";
            graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

            // Instructions
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, 3, "Tab: Next field | Enter: Save | ESC: Cancel");

            // Form
            form.render(graphics, 5, 5);

            // Messages
            messagePanel.render(graphics, 5, size.getRows() - 2);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            messagePanel.clear();

            if (keyStroke.getKeyType() == KeyType.Escape) {
                loadCourses();
                return previousState;
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                return handleSave();
            } else {
                form.handleInput(keyStroke);
                return this;
            }
        }

        private ScreenState handleSave() {
            String code = codeField.getValue().trim();
            String name = nameField.getValue().trim();

            // Validation
            String codeError = new RequiredFieldStrategy("Course code").validate(code);
            if (codeError != null) {
                messagePanel.setError(codeError);
                return this;
            }

            String nameError = new RequiredFieldStrategy("Course name").validate(name);
            if (nameError != null) {
                messagePanel.setError(nameError);
                return this;
            }

            try {
                courseService.createCourse(code, name, currentUser.getId());
                loadCourses();
                previousState.setSuccessMessage("Course created successfully!");
                return new CourseListState();
            } catch (Exception e) {
                messagePanel.setError(e.getMessage());
                return this;
            }
        }

        @Override
        public String getStateName() {
            return "AddCourse";
        }
    }

    /**
     * State 3: Course Details View
     */
    private class CourseDetailsState implements ScreenState {
        private final CourseDTO course;
        private final CourseListState listState;
        private final SelectionList<CourseTaskDTO> taskList;
        private final MessagePanel messagePanel;

        public CourseDetailsState(CourseDTO course, CourseListState listState) {
            this.course = course;
            this.listState = listState;
            this.taskList = new SelectionList<>("Tasks", task ->
                    String.format("%s - %s [%d%%] (%s)",
                            task.getName(),
                            task.getStatus(),
                            task.getProgress(),
                            dateFormat.format(task.getDeadline()))
            );

            if (course.getTasks() != null) {
                taskList.setItems(course.getTasks());
            }
            taskList.setFocused(true);
            messagePanel = new MessagePanel();
        }

        @Override
        public void render(TextGraphics graphics) {
            TerminalSize size = screen.getTerminalSize();

            // Title
            graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            String title = "=== COURSE DETAILS ===";
            graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

            // Instructions
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, 3, "F2: Add Task | F3: Update Progress | ESC: Back");

            // Course info
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            String courseTitle = course.getCode() + " - " + course.getName() +
                    " [" + course.getProgress() + "% complete]";
            graphics.putString(3, 5, "Course: " + courseTitle);

            // Task list
            taskList.render(graphics, 3, 7);

            // Messages
            messagePanel.render(graphics, 3, size.getRows() - 2);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            messagePanel.clear();

            if (keyStroke.getKeyType() == KeyType.F2) {
                return new AddTaskState(course, this);
            } else if (keyStroke.getKeyType() == KeyType.F3) {
                if (taskList.isEmpty()) {
                    messagePanel.setError("No tasks available to update");
                    return this;
                }
                return new UpdateProgressState(course, taskList.getSelectedItem(), this);
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                loadCourses();
                return listState;
            } else {
                taskList.handleInput(keyStroke);
                return this;
            }
        }

        @Override
        public String getStateName() {
            return "CourseDetails";
        }

        public void setSuccessMessage(String message) {
            messagePanel.setSuccess(message);
        }
    }

    /**
     * State 4: Add Task Form
     */
    private class AddTaskState implements ScreenState {
        private final CourseDTO course;
        private final CourseDetailsState previousState;
        private final Form form;
        private final FormField nameField;
        private final FormField descriptionField;
        private final DateInput deadlineInput;
        private final MessagePanel messagePanel;

        public AddTaskState(CourseDTO course, CourseDetailsState previousState) {
            this.course = course;
            this.previousState = previousState;

            nameField = ComponentFactory.createTextField("Task Name");
            descriptionField = ComponentFactory.createTextField("Description");
            deadlineInput = ComponentFactory.createDateInput("Deadline");

            form = new Form()
                    .addField(nameField)
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

            // Title
            graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            String title = "=== ADD NEW TASK ===";
            graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

            // Instructions
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, 3, "Tab: Next field | Enter: Save | ESC: Cancel");

            // Form
            form.render(graphics, 5, 5);

            // Messages
            messagePanel.render(graphics, 5, size.getRows() - 2);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            messagePanel.clear();

            if (keyStroke.getKeyType() == KeyType.Escape) {
                loadCourses();
                return previousState;
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                return handleSave();
            } else {
                form.handleInput(keyStroke);
                return this;
            }
        }

        private ScreenState handleSave() {
            String name = nameField.getValue().trim();
            String description = descriptionField.getValue().trim();
            Date deadline = deadlineInput.getDate();

            // Validation
            String nameError = new RequiredFieldStrategy("Task name").validate(name);
            if (nameError != null) {
                messagePanel.setError(nameError);
                return this;
            }

            if (deadline == null) {
                String error = deadlineInput.getErrorMessage();
                messagePanel.setError(error.isEmpty() ? "All date fields are required" : error);
                return this;
            }

            try {
                courseService.addTaskToCourse(course.getId(), name, deadline, description);
                loadCourses();
                CourseDTO updatedCourse = courses.stream()
                        .filter(c -> c.getId().equals(course.getId()))
                        .findFirst()
                        .orElse(course);
                CourseDetailsState newDetailsState = new CourseDetailsState(updatedCourse, new CourseListState());
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

    /**
     * State 5: Update Progress Form
     */
    private class UpdateProgressState implements ScreenState {
        private final CourseDTO course;
        private final CourseTaskDTO task;
        private final CourseDetailsState previousState;
        private final FormField progressField;
        private final MessagePanel messagePanel;

        public UpdateProgressState(CourseDTO course, CourseTaskDTO task, CourseDetailsState previousState) {
            this.course = course;
            this.task = task;
            this.previousState = previousState;
            this.progressField = ComponentFactory.createNumericField("New Progress (0-100)", 3);
            this.progressField.setFocused(true);
            this.messagePanel = new MessagePanel();
        }

        @Override
        public void render(TextGraphics graphics) {
            TerminalSize size = screen.getTerminalSize();

            // Title
            graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            String title = "=== UPDATE TASK PROGRESS ===";
            graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

            // Instructions
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, 3, "Enter: Save | ESC: Cancel");

            // Task info
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(5, 5, "Task: " + task.getName());
            graphics.putString(5, 6, "Current Progress: " + task.getProgress() + "%");

            // Progress input
            progressField.render(graphics, 5, 8);

            // Messages
            messagePanel.render(graphics, 5, size.getRows() - 2);
        }

        @Override
        public ScreenState handleInput(KeyStroke keyStroke) {
            messagePanel.clear();

            if (keyStroke.getKeyType() == KeyType.Escape) {
                loadCourses();
                return previousState;
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                return handleSave();
            } else {
                progressField.handleInput(keyStroke);
                return this;
            }
        }

        private ScreenState handleSave() {
            String progressStr = progressField.getValue().trim();

            if (progressStr.isEmpty()) {
                messagePanel.setError("Progress value is required");
                return this;
            }

            try {
                int progress = Integer.parseInt(progressStr);
                if (progress < 0 || progress > 100) {
                    messagePanel.setError("Progress must be between 0 and 100");
                    return this;
                }

                courseService.updateTaskProgress(course.getId(), task.getId(), progress);

                if (progress == 100) {
                    courseService.markTaskComplete(course.getId(), task.getId());
                }

                loadCourses();
                CourseDTO updatedCourse = courses.stream()
                        .filter(c -> c.getId().equals(course.getId()))
                        .findFirst()
                        .orElse(course);
                CourseDetailsState newDetailsState = new CourseDetailsState(updatedCourse, new CourseListState());
                newDetailsState.setSuccessMessage(
                        progress == 100 ? "Task marked as complete!" : "Progress updated successfully!"
                );
                return newDetailsState;
            } catch (NumberFormatException e) {
                messagePanel.setError("Invalid progress value");
                return this;
            } catch (Exception e) {
                messagePanel.setError(e.getMessage());
                return this;
            }
        }

        @Override
        public String getStateName() {
            return "UpdateProgress";
        }
    }
}

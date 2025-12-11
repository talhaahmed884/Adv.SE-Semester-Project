package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.CourseMediator;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * State 3: Course Details View
 *
 * Responsibilities:
 * - Display details of a specific course
 * - Handle adding tasks and updating progress
 * - No data ownership - fetches fresh from mediator
 */
public class CourseDetailsState implements ScreenState {
    private final CourseMediator mediator;
    private final UUID courseId;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")
            .withZone(ZoneId.systemDefault());

    private final SelectionList<CourseTaskDTO> taskList;
    private final MessagePanel messagePanel;
    private CourseDTO course; // Cached during render cycle

    public CourseDetailsState(CourseMediator mediator, UUID courseId, String successMessage) {
        this.mediator = mediator;
        this.courseId = courseId;

        this.taskList = new SelectionList<>("Tasks", task ->
                String.format("%s - %s [%d%%] (%s)",
                        task.getName(),
                        task.getStatus(),
                        task.getProgress(),
                        dateFormat.format(task.getDeadline()))
        );
        taskList.setFocused(true);

        messagePanel = new MessagePanel();
        if (successMessage != null) {
            messagePanel.setSuccess(successMessage);
        }
    }

    @Override
    public void onEnter() {
        // Fetch fresh data when entering this state
        course = mediator.getCourseById(courseId);
        if (course.getTasks() != null) {
            taskList.setItems(course.getTasks());
        }
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== COURSE DETAILS ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F2: Add Task | F3: Update Progress | F4: Edit Course | F5: Delete Course");
        graphics.putString(3, 4, "F6: Edit Task | F7: Delete Task | ESC: Back");

        // Course info
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        String courseTitle = course.getCode() + " - " + course.getName() +
                " [" + course.getProgress() + "% complete]";
        graphics.putString(3, 6, "Course: " + courseTitle);

        // Task list
        taskList.render(graphics, 3, 8);

        // Messages
        messagePanel.render(graphics, 3, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.F2) {
            // Notify mediator to show add task form
            mediator.onAddTaskToCourse(courseId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F3) {
            if (taskList.isEmpty()) {
                messagePanel.setError("No tasks available to update");
                return this;
            }
            // Notify mediator to show update progress form
            CourseTaskDTO selectedTask = taskList.getSelectedItem();
            mediator.onUpdateTaskProgress(courseId, selectedTask.getId());
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F4) {
            // Notify mediator to show edit course form
            mediator.onEditCourse(courseId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F5) {
            // Notify mediator to show delete course confirmation
            mediator.onDeleteCourse(courseId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F6) {
            if (taskList.isEmpty()) {
                messagePanel.setError("No tasks available to edit");
                return this;
            }
            // Notify mediator to show edit task form
            CourseTaskDTO selectedTask = taskList.getSelectedItem();
            mediator.onEditTask(courseId, selectedTask.getId());
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F7) {
            if (taskList.isEmpty()) {
                messagePanel.setError("No tasks available to delete");
                return this;
            }
            // Notify mediator to show delete task confirmation
            CourseTaskDTO selectedTask = taskList.getSelectedItem();
            mediator.onDeleteTask(courseId, selectedTask.getId());
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to course list
            mediator.onReturnToCourseList();
            return null; // Mediator handles transition
        } else {
            taskList.handleInput(keyStroke);
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "CourseDetails";
    }
}

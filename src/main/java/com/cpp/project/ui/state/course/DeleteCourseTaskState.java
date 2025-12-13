package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.CourseMediator;
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
public class DeleteCourseTaskState implements ScreenState {
    private final CourseMediator mediator;
    private final CourseService courseService;
    private final UUID courseId;
    private final UUID taskId;
    private final boolean fromTaskDetails;
    private final SelectionList<String> optionsList;
    private final MessagePanel messagePanel;
    private CourseTaskDTO task; // Cached task data

    private static final String CONFIRM = "Yes, Delete Task";
    private static final String CANCEL = "No, Cancel";

    public DeleteCourseTaskState(CourseMediator mediator, CourseService courseService, UUID courseId, UUID taskId, boolean fromTaskDetails) {
        this.mediator = mediator;
        this.courseService = courseService;
        this.courseId = courseId;
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
        CourseDTO course = mediator.getCourseById(courseId);
        task = course.getTasks().stream()
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
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Warning
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "WARNING: This action cannot be undone!");

        if (task != null) {
            // Task info
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(3, 5, "Task Name: " + task.getName());
            graphics.putString(3, 6, "Description: " + (task.getDescription() != null ? task.getDescription() : "N/A"));
            graphics.putString(3, 7, "Deadline: " + task.getDeadline().toString());
            graphics.putString(3, 8, "Progress: " + task.getProgress() + "%");
            graphics.putString(3, 9, "Status: " + task.getStatus());
        }

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 11, "Are you sure you want to delete this task?");

        // Options
        optionsList.render(graphics, 3, 13);

        // Messages
        messagePanel.render(graphics, 3, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to course details
            mediator.onViewCourseDetails(courseId);
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
            // User cancelled, return to course details
            mediator.onViewCourseDetails(courseId);
            return null; // Mediator handles transition
        } else if (CONFIRM.equals(selected)) {
            // User confirmed, delete the task
            try {
                courseService.deleteTask(courseId, taskId);
                // Notify mediator - it will transition to course details with success message
                mediator.onTaskDeleted(courseId);
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

package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.cpp.project.ui.mediator.CourseMediator;
import com.cpp.project.ui.util.UILayoutConstants;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.UUID;

/**
 * State 5: Update Progress Form
 * <p>
 * Responsibilities:
 * - Collect new progress value from user
 * - Update task progress via service
 * - Notify mediator on success or cancellation
 */
public class UpdateProgressState implements ScreenState {
    private final CourseMediator mediator;
    private final CourseService courseService;
    private final UUID courseId;
    private final UUID taskId;
    private final boolean fromTaskDetails;
    private final FormField progressField;
    private final MessagePanel messagePanel;
    private CourseTaskDTO task; // Cached during render cycle

    public UpdateProgressState(CourseMediator mediator, CourseService courseService,
                               UUID courseId, UUID taskId, boolean fromTaskDetails) {
        this.mediator = mediator;
        this.courseService = courseService;
        this.courseId = courseId;
        this.taskId = taskId;
        this.fromTaskDetails = fromTaskDetails;
        this.progressField = ComponentFactory.createNumericField("New Progress (0-100)", 3);
        this.progressField.setFocused(true);
        this.messagePanel = new MessagePanel();
    }

    @Override
    public void onEnter() {
        // Fetch fresh course data to get the task
        CourseDTO course = mediator.getCourseById(courseId);
        task = course.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Task not found"));
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== UPDATE TASK PROGRESS ===";
        graphics.putString(UILayoutConstants.centerX(size, title.length()), UILayoutConstants.TITLE_ROW, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW, "Enter: Save | ESC: Cancel");

        // Task info
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(UILayoutConstants.FORM_LEFT, UILayoutConstants.FORM_START_ROW, "Task: " + task.getName());
        graphics.putString(UILayoutConstants.FORM_LEFT, 6, "Current Progress: " + task.getProgress() + "%");

        // Progress input
        progressField.render(graphics, UILayoutConstants.FORM_LEFT, 8);

        // Messages
        messagePanel.render(graphics, UILayoutConstants.FORM_LEFT, UILayoutConstants.messageRow(size));
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.Escape) {
            // Notify mediator to return to course details
            mediator.onViewCourseDetails(courseId);
            return null; // Mediator handles transition
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

            courseService.updateTaskProgress(courseId, taskId, progress);

            boolean wasCompleted = false;
            if (progress == 100) {
                courseService.markTaskComplete(courseId, taskId);
                wasCompleted = true;
            }

            // Notify mediator - it will transition based on context
            if (fromTaskDetails) {
                mediator.onTaskProgressUpdatedReturnToTaskDetails(courseId, taskId, wasCompleted);
            } else {
                mediator.onTaskProgressUpdated(courseId, wasCompleted);
            }
            return null; // Mediator handles transition
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

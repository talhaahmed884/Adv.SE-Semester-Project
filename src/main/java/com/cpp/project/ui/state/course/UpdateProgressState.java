package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.ui.component.FormField;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.factory.ComponentFactory;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

/**
 * State 5: Update Progress Form
 */
public class UpdateProgressState implements ScreenState {
    private final Screen screen;
    private final CourseDTO course;
    private final CourseTaskDTO task;
    private final CourseDetailsState previousState;
    private final CourseService courseService;
    private final FormField progressField;
    private final MessagePanel messagePanel;
    private boolean wasCompleted = false;

    public UpdateProgressState(Screen screen, CourseDTO course, CourseTaskDTO task,
                               CourseDetailsState previousState, CourseService courseService) {
        this.screen = screen;
        this.course = course;
        this.task = task;
        this.previousState = previousState;
        this.courseService = courseService;
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
            // Adapter will handle reload if needed
            return previousState;
        } else if (keyStroke.getKeyType() == KeyType.Enter) {
            return handleSave();
        } else {
            progressField.handleInput(keyStroke);
            return this;
        }
    }

    public boolean wasTaskCompleted() {
        return wasCompleted;
    }

    public CourseDTO getCourse() {
        return course;
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
                wasCompleted = true;
            }

            // Return null to signal adapter to reload and recreate states
            return null;
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

package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.TimerSummaryPanel;
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
 * State for viewing detailed information about a course task
 * <p>
 * Responsibilities:
 * - Display task details (name, description, deadline, status, progress)
 * - Display timer summary and session information
 * - Provide access to task operations (update progress, edit, delete, view timer)
 * - No data ownership - fetches fresh from mediator
 */
public class TaskDetailsState implements ScreenState {
    private final CourseMediator mediator;
    private final UUID courseId;
    private final UUID taskId;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")
            .withZone(ZoneId.systemDefault());

    private final TimerSummaryPanel timerPanel;
    private final MessagePanel messagePanel;

    private CourseTaskDTO task;
    private TaskTimerSummaryDTO timerSummary;

    public TaskDetailsState(CourseMediator mediator, UUID courseId, UUID taskId, String successMessage) {
        this.mediator = mediator;
        this.courseId = courseId;
        this.taskId = taskId;

        this.timerPanel = new TimerSummaryPanel(null);
        this.messagePanel = new MessagePanel();

        if (successMessage != null) {
            messagePanel.setSuccess(successMessage);
        }
    }

    @Override
    public void onEnter() {
        // Fetch fresh course data
        CourseDTO course = mediator.getCourseById(courseId);

        // Extract the task from the course
        if (course.getTasks() != null) {
            task = course.getTasks().stream()
                    .filter(t -> t.getId().equals(taskId))
                    .findFirst()
                    .orElse(null);
        }

        // Handle task not found (may have been deleted)
        if (task == null) {
            messagePanel.setError("Task not found. Returning to course details...");
            // Return to course details after a brief moment
            mediator.onViewCourseDetails(courseId);
            return;
        }

        // Load timer summary
        loadTimerSummary();
    }

    private void loadTimerSummary() {
        try {
            timerSummary = mediator.getTimerSummary(taskId);
            timerPanel.setSummary(timerSummary);
        } catch (Exception e) {
            // Timer data is optional - silently ignore errors
            timerSummary = null;
            timerPanel.setSummary(null);
        }
    }

    @Override
    public void render(TextGraphics graphics) {
        if (task == null) {
            // Task not found - show error
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(3, 3, "Task not found. Press ESC to return.");
            return;
        }

        TerminalSize size = graphics.getSize();

        // Title with task name
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String taskName = task.getName();
        if (taskName.length() > 60) {
            taskName = taskName.substring(0, 57) + "...";
        }
        String title = "=== TASK DETAILS: " + taskName + " ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F3: Update Progress | F4: Edit | F5: Delete | F6: Timer | ESC: Back");

        // Section: Task Information
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(3, 5, "Task Information");
        graphics.putString(3, 6, "----------------------------------------");

        // Task details
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        int currentY = 7;

        graphics.putString(5, currentY++, "Name: " + task.getName());

        // Description (handle null and long text)
        String description = task.getDescription();
        if (description != null && !description.trim().isEmpty()) {
            graphics.putString(5, currentY++, "Description: " + wrapText(description, 70));
        } else {
            graphics.putString(5, currentY++, "Description: (none)");
        }

        graphics.putString(5, currentY++, "Deadline: " + dateFormat.format(task.getDeadline()));
        graphics.putString(5, currentY++, "Status: " + task.getStatus());

        // Progress with visual indicator
        String progressBar = createProgressBar(task.getProgress());
        graphics.putString(5, currentY++, "Progress: " + task.getProgress() + "% " + progressBar);

        currentY++; // Spacing

        // Section: Timer Summary
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(3, currentY++, "Timer Summary");
        graphics.putString(3, currentY++, "----------------------------------------");

        // Render timer panel
        timerPanel.render(graphics, 5, currentY);

        // Messages at bottom
        messagePanel.render(graphics, 3, size.getRows() - 2);
    }

    /**
     * Wraps text to fit within specified width
     */
    private String wrapText(String text, int maxWidth) {
        if (text.length() <= maxWidth) {
            return text;
        }
        return text.substring(0, maxWidth - 3) + "...";
    }

    /**
     * Creates a visual progress bar
     */
    private String createProgressBar(int progress) {
        int barLength = 20;
        int filled = (progress * barLength) / 100;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("=");
            } else {
                bar.append(" ");
            }
        }
        bar.append("]");
        return bar.toString();
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (task == null) {
            // Task not found - only allow ESC
            if (keyStroke.getKeyType() == KeyType.Escape) {
                mediator.onViewCourseDetails(courseId);
                return null;
            }
            return this;
        }

        if (keyStroke.getKeyType() == KeyType.F3) {
            // Update progress
            mediator.onUpdateTaskProgressFromTaskDetails(courseId, taskId);
            return null;
        } else if (keyStroke.getKeyType() == KeyType.F4) {
            // Edit task
            mediator.onEditTaskFromTaskDetails(courseId, taskId);
            return null;
        } else if (keyStroke.getKeyType() == KeyType.F5) {
            // Delete task
            mediator.onDeleteTaskFromTaskDetails(courseId, taskId);
            return null;
        } else if (keyStroke.getKeyType() == KeyType.F6) {
            // View timer details
            mediator.onViewTimerDetails(courseId, taskId);
            return null;
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            // Return to course details
            mediator.onViewCourseDetails(courseId);
            return null;
        } else {
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "TaskDetails";
    }
}

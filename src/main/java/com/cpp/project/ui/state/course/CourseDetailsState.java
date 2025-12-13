package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.timer.entity.TimerStatus;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.CourseMediator;
import com.cpp.project.ui.util.TimerFormatUtils;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * State 3: Course Details View
 * <p>
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
    private Map<UUID, TaskTimerSummaryDTO> timerSummaries; // Timer data for each task

    public CourseDetailsState(CourseMediator mediator, UUID courseId, String successMessage) {
        this.mediator = mediator;
        this.courseId = courseId;
        this.timerSummaries = new HashMap<>();

        this.taskList = new SelectionList<>("Tasks", this::formatTaskWithTimer
        );
        taskList.setFocused(true);

        messagePanel = new MessagePanel();
        if (successMessage != null) {
            messagePanel.setSuccess(successMessage);
        }
    }

    /**
     * Format task display with timer information
     */
    private String formatTaskWithTimer(CourseTaskDTO task) {
        StringBuilder display = new StringBuilder();

        // Basic task info
        display.append(String.format("%s - %s [%d%%] (%s)",
                task.getName(),
                task.getStatus(),
                task.getProgress(),
                dateFormat.format(task.getDeadline())));

        // Add timer information if available
        TaskTimerSummaryDTO timerSummary = timerSummaries.get(task.getId());
        if (timerSummary != null) {
            // Show total time
            if (timerSummary.getTotalTimeMillis() > 0) {
                display.append(" [Timer: ")
                        .append(TimerFormatUtils.formatDuration(timerSummary.getTotalTimeMillis()))
                        .append("]");
            }

            // Show running indicator
            if (timerSummary.getActiveSession() != null &&
                    timerSummary.getActiveSession().getStatus() == TimerStatus.RUNNING) {
                display.append(" [RUNNING]");
            }
        }

        return display.toString();
    }

    @Override
    public void onEnter() {
        // Fetch fresh data when entering this state
        course = mediator.getCourseById(courseId);
        if (course.getTasks() != null) {
            // Load timer summaries for all tasks
            loadTimerSummaries();

            taskList.setItems(course.getTasks());
        }
    }

    /**
     * Load timer summaries for all tasks in the course
     */
    private void loadTimerSummaries() {
        timerSummaries.clear();
        if (course.getTasks() != null) {
            for (CourseTaskDTO task : course.getTasks()) {
                try {
                    TaskTimerSummaryDTO summary = mediator.getTimerSummary(task.getId());
                    if (summary != null) {
                        timerSummaries.put(task.getId(), summary);
                    }
                } catch (Exception e) {
                    // Silently ignore - timer data is optional
                }
            }
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
        graphics.putString(3, 3, "Enter: View Task Details | F2: Add Task | F4: Edit Course | F5: Delete Course | ESC: Back");

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

        if (keyStroke.getKeyType() == KeyType.Enter) {
            if (taskList.isEmpty()) {
                messagePanel.setError("No tasks available");
                return this;
            }
            // Notify mediator to show task details view
            CourseTaskDTO selectedTask = taskList.getSelectedItem();
            mediator.onViewTaskDetails(courseId, selectedTask.getId());
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F2) {
            // Notify mediator to show add task form
            mediator.onAddTaskToCourse(courseId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F4) {
            // Notify mediator to show edit course form
            mediator.onEditCourse(courseId);
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F5) {
            // Notify mediator to show delete course confirmation
            mediator.onDeleteCourse(courseId);
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

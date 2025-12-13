package com.cpp.project.ui.state.course;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.entity.TimerException;
import com.cpp.project.timer.entity.TimerStatus;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.component.TimerSummaryPanel;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.CourseMediator;
import com.cpp.project.ui.util.TimerFormatUtils;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.List;
import java.util.UUID;

/**
 * State for viewing and controlling timers for a course task
 * <p>
 * Responsibilities:
 * - Display timer summary and session history
 * - Handle start/stop timer actions
 * - Refresh timer data
 * - No data ownership - fetches fresh from mediator/service
 */
public class TimerViewState implements ScreenState {
    private final CourseMediator mediator;
    private final TimerService timerService;
    private final UserDTO currentUser;
    private final UUID courseId;
    private final UUID taskId;

    private final TimerSummaryPanel summaryPanel;
    private final SelectionList<TimerDTO> sessionList;
    private final MessagePanel messagePanel;

    private TaskTimerSummaryDTO summary;
    private CourseTaskDTO task;

    public TimerViewState(CourseMediator mediator, TimerService timerService, UserDTO currentUser,
                          UUID courseId, UUID taskId, String message) {
        this.mediator = mediator;
        this.timerService = timerService;
        this.currentUser = currentUser;
        this.courseId = courseId;
        this.taskId = taskId;

        // Initialize components
        this.summaryPanel = new TimerSummaryPanel(null);
        this.sessionList = new SelectionList<>("Session History",
                timer -> TimerFormatUtils.formatTimerSession(
                        summary != null ? summary.getSessions().indexOf(timer) + 1 : 1,
                        timer
                )
        );
        this.sessionList.setFocused(false); // Not interactive for now

        this.messagePanel = new MessagePanel();
        if (message != null) {
            if (message.contains("success") || message.contains("started") || message.contains("stopped")) {
                messagePanel.setSuccess(message);
            } else {
                messagePanel.setError(message);
            }
        }
    }

    @Override
    public void onEnter() {
        // Fetch fresh data when entering this state
        loadTimerData();
        loadTaskData();
    }

    private void loadTimerData() {
        try {
            summary = timerService.getTimerSummaryByTaskId(taskId);
            summaryPanel.setSummary(summary);

            if (summary != null && summary.getSessions() != null) {
                // Reverse the list so newest sessions appear first
                List<TimerDTO> sessions = summary.getSessions();
                java.util.Collections.reverse(sessions);
                sessionList.setItems(sessions);
            }
        } catch (Exception e) {
            messagePanel.setError("Failed to load timer data: " + e.getMessage());
        }
    }

    private void loadTaskData() {
        try {
            CourseDTO course = mediator.getCourseById(courseId);
            if (course != null && course.getTasks() != null) {
                task = course.getTasks().stream()
                        .filter(t -> t.getId().equals(taskId))
                        .findFirst()
                        .orElse(null);
            }
        } catch (Exception e) {
            messagePanel.setError("Failed to load task data: " + e.getMessage());
        }
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String taskName = task != null ? task.getName() : "Unknown Task";
        String title = "=== TIMER - " + taskName + " ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F1: Start Timer | F2: Stop Timer | F3: Refresh | ESC: Back");

        // Timer summary
        summaryPanel.render(graphics, 3, 5);

        // Running timer details (if applicable)
        int nextY = 9;
        if (summary != null && summary.getActiveSession() != null) {
            TimerDTO activeSession = summary.getActiveSession();
            if (activeSession.getStatus() == TimerStatus.RUNNING) {
                graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
                String runningText = "[RUNNING] Started: " +
                        TimerFormatUtils.formatTimestamp(activeSession.getStartTime()) +
                        " | Elapsed: " +
                        TimerFormatUtils.formatDuration(
                                TimerFormatUtils.calculateElapsedMillis(activeSession.getStartTime())
                        );
                graphics.putString(3, nextY, runningText);
                nextY += 2;
            }
        }

        // Session history
        if (summary != null && summary.getSessions() != null && !summary.getSessions().isEmpty()) {
            sessionList.render(graphics, 3, nextY);
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(3, nextY, "No timer sessions yet");
        }

        // Messages at bottom
        messagePanel.render(graphics, 3, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.F1) {
            // Start timer
            handleStartTimer();
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F2) {
            // Stop timer
            handleStopTimer();
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.F3) {
            // Refresh
            loadTimerData();
            messagePanel.setSuccess("Timer data refreshed");
            return this;
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            // Return to course details
            mediator.onViewCourseDetails(courseId);
            return null; // Mediator handles transition
        } else {
            // Allow scrolling through session history
            sessionList.handleInput(keyStroke);
            return this;
        }
    }

    private void handleStartTimer() {
        try {
            // Check if timer already running
            if (summary != null && summary.getActiveSession() != null &&
                    summary.getActiveSession().getStatus() == TimerStatus.RUNNING) {
                messagePanel.setError("Timer is already running for this task. Press F2 to stop it first.");
                return;
            }

            // Start timer
            timerService.startTimer(currentUser.getId(), taskId);

            // Notify mediator of success
            mediator.onTimerStarted(courseId, taskId);
        } catch (TimerException e) {
            // Handle timer-specific exceptions
            handleTimerException(e);
        } catch (Exception e) {
            messagePanel.setError("Failed to start timer: " + e.getMessage());
        }
    }

    private void handleStopTimer() {
        try {
            // Check if there's an active timer
            if (summary == null || summary.getActiveSession() == null ||
                    summary.getActiveSession().getStatus() != TimerStatus.RUNNING) {
                messagePanel.setError("No timer is currently running for this task.");
                return;
            }

            // Get active timer ID
            UUID timerId = summary.getActiveSession().getId();

            // Stop timer
            timerService.stopTimer(timerId, currentUser.getId());

            // Notify mediator of success
            mediator.onTimerStopped(courseId, taskId);
        } catch (TimerException e) {
            // Handle timer-specific exceptions
            handleTimerException(e);
        } catch (Exception e) {
            messagePanel.setError("Failed to stop timer: " + e.getMessage());
        }
    }

    private void handleTimerException(TimerException e) {
        // Extract user-friendly error message from exception
        String errorMessage = e.getMessage();

        // Provide more helpful context based on error code
        if (errorMessage.contains("already running")) {
            messagePanel.setError("Timer is already running for this task. Press F2 to stop it first.");
        } else if (errorMessage.contains("not running")) {
            messagePanel.setError("No timer is currently running for this task.");
        } else if (errorMessage.contains("not found")) {
            messagePanel.setError("Timer not found. Please refresh (F3) and try again.");
        } else if (errorMessage.contains("not authorized")) {
            messagePanel.setError("You are not authorized to modify this timer.");
        } else {
            messagePanel.setError("Timer operation failed: " + errorMessage);
        }
    }

    @Override
    public void onExit() {
        // No cleanup needed - timer continues running in background
    }

    @Override
    public String getStateName() {
        return "TimerView";
    }
}

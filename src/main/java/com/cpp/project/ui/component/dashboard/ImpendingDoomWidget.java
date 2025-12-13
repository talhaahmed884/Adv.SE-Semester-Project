package com.cpp.project.ui.component.dashboard;

import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.dashboard.dto.UpcomingTaskDTO;
import com.cpp.project.dashboard.entity.TaskUrgency;
import com.cpp.project.ui.component.AbstractComponent;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Widget for displaying upcoming tasks with urgency-based color coding
 * "Impending Doom Timeline" - Shows tasks due in the next 7 days
 * Component Pattern - Reusable UI component
 * Uses color coding: Red = Today, Yellow = Tomorrow, White = This Week
 */
public class ImpendingDoomWidget extends AbstractComponent {
    private static final int HEADER_HEIGHT = 3;
    private static final int MAX_TASKS_DISPLAYED = 15;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, h:mm a").withZone(ZoneId.systemDefault());

    private List<UpcomingTaskDTO> upcomingTasks;

    public ImpendingDoomWidget(List<UpcomingTaskDTO> upcomingTasks) {
        super(calculateHeight(upcomingTasks));
        this.upcomingTasks = upcomingTasks;
    }

    /**
     * Calculate component height based on number of tasks
     */
    private static int calculateHeight(List<UpcomingTaskDTO> upcomingTasks) {
        int taskCount = upcomingTasks != null ?
                Math.min(upcomingTasks.size(), MAX_TASKS_DISPLAYED) : 0;
        return HEADER_HEIGHT + taskCount + 1; // +1 for footer/spacing
    }

    /**
     * Update the upcoming tasks data
     */
    public void setUpcomingTasks(List<UpcomingTaskDTO> upcomingTasks) {
        this.upcomingTasks = upcomingTasks;
        this.height = calculateHeight(upcomingTasks);
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        if (upcomingTasks == null || upcomingTasks.isEmpty()) {
            renderNoData(graphics, x, y);
            return;
        }

        // Title
        graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
        graphics.putString(x, y, "╔══════════════════════════════════════════════════════════════╗");
        graphics.putString(x, y + 1, "║  IMPENDING DOOM - Tasks Due in Next 7 Days                   ║");
        graphics.putString(x, y + 2, "╚══════════════════════════════════════════════════════════════╝");

        int currentY = y + HEADER_HEIGHT;

        // Display each task with color-coded urgency
        int displayCount = Math.min(upcomingTasks.size(), MAX_TASKS_DISPLAYED);
        for (int i = 0; i < displayCount; i++) {
            UpcomingTaskDTO task = upcomingTasks.get(i);
            renderTaskLine(graphics, x, currentY, task, i + 1);
            currentY++;
        }

        // Show warning if there are more tasks
        if (upcomingTasks.size() > MAX_TASKS_DISPLAYED) {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            String moreText = String.format("... and %d more tasks",
                    upcomingTasks.size() - MAX_TASKS_DISPLAYED);
            graphics.putString(x, currentY, moreText);
        }
    }

    /**
     * Render a single task line with color-coded urgency
     */
    private void renderTaskLine(TextGraphics graphics, int x, int y, UpcomingTaskDTO task, int index) {
        // Set color based on urgency
        TextColor color = getUrgencyColor(task.getUrgency());
        graphics.setForegroundColor(color);

        // Format urgency indicator
        String urgencyIndicator = getUrgencyIndicator(task.getUrgency());

        // Format deadline
        String deadlineStr = DATE_FORMATTER.format(task.getDeadline());

        // Format task name (truncate if too long)
        String taskName = task.getTaskName();
        if (taskName.length() > 25) {
            taskName = taskName.substring(0, 22) + "...";
        }

        // Format source (truncate if too long)
        String source = String.format("[%s: %s]", task.getSourceType(), task.getSourceName());
        if (source.length() > 20) {
            source = source.substring(0, 17) + "...]";
        }

        // Status indicator
        String statusIndicator = getStatusIndicator(task.getStatus());

        // Build the line
        String taskLine = String.format("%s %2d. %-25s %-20s %s %s",
                urgencyIndicator,
                index,
                taskName,
                source,
                deadlineStr,
                statusIndicator
        );

        graphics.putString(x, y, taskLine);
    }

    /**
     * Get color based on task urgency
     * Strategy Pattern - Urgency color determination
     */
    private TextColor getUrgencyColor(TaskUrgency urgency) {
        return switch (urgency) {
            case TODAY -> TextColor.ANSI.RED_BRIGHT;
            case TOMORROW -> TextColor.ANSI.YELLOW_BRIGHT;
            case THIS_WEEK -> TextColor.ANSI.WHITE;
        };
    }

    /**
     * Get visual indicator for urgency level
     */
    private String getUrgencyIndicator(TaskUrgency urgency) {
        return switch (urgency) {
            case TODAY -> "🔴";      // Red circle (highest urgency)
            case TOMORROW -> "🟡";   // Yellow circle (medium urgency)
            case THIS_WEEK -> "⚪";  // White circle (low urgency)
        };
    }

    /**
     * Get status indicator symbol
     */
    private String getStatusIndicator(TaskStatus status) {
        return switch (status) {
            case PENDING -> "[ ]";
            case IN_PROGRESS -> "[~]";
            case COMPLETED -> "[✓]";
        };
    }

    private void renderNoData(TextGraphics graphics, int x, int y) {
        graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
        graphics.putString(x, y, "╔══════════════════════════════════════════════════════════════╗");
        graphics.putString(x, y + 1, "║  IMPENDING DOOM - Tasks Due in Next 7 Days                  ║");
        graphics.putString(x, y + 2, "╚══════════════════════════════════════════════════════════════╝");

        graphics.setForegroundColor(TextColor.ANSI.GREEN);
        graphics.putString(x, y + 3, "No upcoming tasks! Enjoy your free time! 🎉");
    }

    @Override
    public boolean handleInput(KeyStroke keyStroke) {
        // This component doesn't handle input (display only)
        return false;
    }
}

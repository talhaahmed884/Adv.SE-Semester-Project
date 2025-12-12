package com.cpp.project.ui.component;

import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.entity.TimerStatus;
import com.cpp.project.ui.util.TimerFormatUtils;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Component for displaying timer summary information
 * Shows total time, session count, and active timer status
 */
public class TimerSummaryPanel extends AbstractComponent {
    private static final int COMPONENT_HEIGHT = 3;
    private TaskTimerSummaryDTO summary;

    public TimerSummaryPanel(TaskTimerSummaryDTO summary) {
        super(COMPONENT_HEIGHT);
        this.summary = summary;
    }

    /**
     * Update the summary data
     */
    public void setSummary(TaskTimerSummaryDTO summary) {
        this.summary = summary;
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        if (summary == null) {
            renderNoData(graphics, x, y);
            return;
        }

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        graphics.putString(x, y, "Timer Summary");

        // Separator
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(x, y + 1, "-----------------------------------------");

        // Summary line: Total Time | Sessions | Status
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        StringBuilder summaryLine = new StringBuilder();
        summaryLine.append("Total Time: ");

        // Format total time
        if (summary.getTotalTimeMillis() > 0) {
            summaryLine.append(TimerFormatUtils.formatDuration(summary.getTotalTimeMillis()));
        } else {
            summaryLine.append("0s");
        }

        summaryLine.append(" | Sessions: ").append(summary.getSessionCount());

        // Add active timer status if timer is running
        TimerDTO activeSession = summary.getActiveSession();
        if (activeSession != null && activeSession.getStatus() == TimerStatus.RUNNING) {
            summaryLine.append(" | Status: ");
            graphics.putString(x, y + 2, summaryLine.toString());

            // Color the status part green
            graphics.setForegroundColor(TextColor.ANSI.GREEN);
            int statusX = x + summaryLine.length();
            long elapsed = TimerFormatUtils.calculateElapsedMillis(activeSession.getStartTime());
            String statusText = "Running (" + TimerFormatUtils.formatDuration(elapsed) + " elapsed)";
            graphics.putString(statusX, y + 2, statusText);
        } else {
            summaryLine.append(" | Status: Not Running");
            graphics.putString(x, y + 2, summaryLine.toString());
        }
    }

    private void renderNoData(TextGraphics graphics, int x, int y) {
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        graphics.putString(x, y, "Timer Summary");

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(x, y + 1, "-----------------------------------------");
        graphics.putString(x, y + 2, "No timer data available");
    }

    @Override
    public boolean handleInput(KeyStroke keyStroke) {
        // This component doesn't handle input (display only)
        return false;
    }
}

package com.cpp.project.ui.component.calendar;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.ui.strategy.RenderingStrategy;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Strategy Pattern: Task List Rendering Strategy
 * Encapsulates the logic for rendering the task list
 */
public class TaskListRenderer implements RenderingStrategy {
    private static final int MAX_TASKS = 8;
    private final List<CalendarItemDTO> items;
    private final DateTimeFormatter dateFormat;

    public TaskListRenderer(List<CalendarItemDTO> items, DateTimeFormatter dateFormat) {
        this.items = items;
        this.dateFormat = dateFormat;
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        graphics.putString(x, y, "Tasks this month:");

        if (items.isEmpty()) {
            renderEmptyMessage(graphics, x, y + 2);
        } else {
            renderTasks(graphics, x + 2, y + 2);
        }
    }

    private void renderEmptyMessage(TextGraphics graphics, int x, int y) {
        graphics.setForegroundColor(TextColor.ANSI.RED);
        graphics.putString(x, y, "No tasks scheduled for this month");
    }

    private void renderTasks(TextGraphics graphics, int x, int y) {
        int currentY = y;
        int taskCount = Math.min(items.size(), MAX_TASKS);

        for (int i = 0; i < taskCount; i++) {
            CalendarItemDTO item = items.get(i);
            renderTask(graphics, x, currentY, item);
            currentY++;
        }

        // Show overflow indicator
        if (items.size() > MAX_TASKS) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(x, currentY + 1, "... and " + (items.size() - MAX_TASKS) + " more tasks");
        }

        // Render legend
        renderLegend(graphics, x, currentY + 3);
    }

    private void renderTask(TextGraphics graphics, int x, int y, CalendarItemDTO item) {
        // Format: Date | Type | Title | Status
        String taskLine = String.format("%s | %-10s | %-25s | %s",
                dateFormat.format(item.getDate()),
                item.getSourceType(),
                truncate(item.getTitle(), 25),
                item.getStatus());

        // Color code by source type
        TextColor color = "COURSE".equals(item.getSourceType()) ?
                TextColor.ANSI.BLUE_BRIGHT : TextColor.ANSI.MAGENTA_BRIGHT;
        graphics.setForegroundColor(color);
        graphics.putString(x, y, taskLine);
    }

    private void renderLegend(TextGraphics graphics, int x, int y) {
        graphics.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT);
        graphics.putString(x, y, "Blue: Course tasks");
        graphics.setForegroundColor(TextColor.ANSI.MAGENTA_BRIGHT);
        graphics.putString(x + 25, y, "Magenta: To-Do tasks");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}

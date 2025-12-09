package com.cpp.project.ui.state.calendar;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.ui.component.calendar.CalendarGridRenderer;
import com.cpp.project.ui.component.calendar.TaskListRenderer;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.CalendarMediator;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * State: Month View
 * <p>
 * Responsibilities:
 * - Display calendar grid for current month
 * - Display tasks for current month
 * - Handle navigation to previous/next month
 * - Cache renderer objects for performance
 */
public class MonthViewState implements ScreenState {
    private final CalendarMediator mediator;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

    // Cached renderers - created once, reused for performance
    private CalendarGridRenderer gridRenderer;
    private TaskListRenderer taskListRenderer;

    // Current data
    private List<CalendarItemDTO> calendarItems;
    private int currentYear;
    private int currentMonth;

    public MonthViewState(CalendarMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void onEnter() {
        // Fetch fresh data from mediator when entering this state
        currentYear = mediator.getCurrentYear();
        currentMonth = mediator.getCurrentMonth();
        calendarItems = mediator.getItemsForMonth(currentYear, currentMonth,
                java.util.UUID.randomUUID()); // Will be updated to use actual user ID

        // Create renderers once and cache them (performance optimization)
        gridRenderer = new CalendarGridRenderer(currentYear, currentMonth, calendarItems);
        taskListRenderer = new TaskListRenderer(calendarItems, dateFormat);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== MONTHLY CALENDAR ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Navigation instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Arrow Left: Previous Month | Arrow Right: Next Month | ESC: Back");

        // Current month/year
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        String[] monthNames = mediator.getMonthNames();
        String monthYear = monthNames[currentMonth - 1] + " " + currentYear;
        graphics.putString((size.getColumns() - monthYear.length()) / 2, 5, monthYear);

        // Use cached renderers for performance
        if (gridRenderer != null) {
            gridRenderer.render(graphics, 5, 7);
        }

        if (taskListRenderer != null) {
            taskListRenderer.render(graphics, 3, 20);
        }
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
            // Notify mediator to navigate to previous month
            mediator.onPreviousMonth();
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
            // Notify mediator to navigate to next month
            mediator.onNextMonth();
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            mediator.onReturnToMainMenu();
            return null; // Mediator handles transition
        }

        return this; // Stay in this state
    }

    @Override
    public String getStateName() {
        return "MonthView";
    }

    /**
     * Update the state with new data (called by mediator after month navigation)
     */
    public void updateMonth() {
        currentYear = mediator.getCurrentYear();
        currentMonth = mediator.getCurrentMonth();
        calendarItems = mediator.getItemsForMonth(currentYear, currentMonth,
                java.util.UUID.randomUUID()); // Will be updated to use actual user ID

        // Update cached renderers with new data
        gridRenderer = new CalendarGridRenderer(currentYear, currentMonth, calendarItems);
        taskListRenderer = new TaskListRenderer(calendarItems, dateFormat);
    }
}

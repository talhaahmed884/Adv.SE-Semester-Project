package com.cpp.project.ui.screen;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.ui.component.calendar.CalendarGridRenderer;
import com.cpp.project.ui.component.calendar.TaskListRenderer;
import com.cpp.project.ui.core.UIScreen;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Refactored Calendar Screen using design patterns:
 * - Template Method Pattern: Extends UIScreen
 * - Strategy Pattern: Separate rendering strategies for grid and tasks
 * - Single Responsibility: Separated calendar grid logic from task list logic
 */
public class CalendarScreen extends UIScreen {
    private final UserDTO currentUser;
    private final CalendarService calendarService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private final String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private int currentYear;
    private int currentMonth; // 1-12
    private List<CalendarItemDTO> calendarItems;

    // Rendering strategies
    private CalendarGridRenderer gridRenderer;
    private TaskListRenderer taskListRenderer;

    public CalendarScreen(Screen screen, UserDTO currentUser, CalendarService calendarService) {
        super(screen);
        this.currentUser = currentUser;
        this.calendarService = calendarService;

        // Initialize to current month
        Calendar cal = Calendar.getInstance();
        this.currentYear = cal.get(Calendar.YEAR);
        this.currentMonth = cal.get(Calendar.MONTH) + 1;

        loadCalendarItems();
    }

    @Override
    protected void render() {
        TextGraphics graphics = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();

        // Title
        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== MONTHLY CALENDAR ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        // Navigation instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "Arrow Left: Previous Month | Arrow Right: Next Month | ESC: Back");

        // Current month/year
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        String monthYear = monthNames[currentMonth - 1] + " " + currentYear;
        graphics.putString((size.getColumns() - monthYear.length()) / 2, 5, monthYear);

        // Use strategy pattern for rendering
        gridRenderer = new CalendarGridRenderer(currentYear, currentMonth, calendarItems);
        gridRenderer.render(graphics, 5, 7);

        taskListRenderer = new TaskListRenderer(calendarItems, dateFormat);
        taskListRenderer.render(graphics, 3, 20);
    }

    @Override
    protected void handleInput() throws IOException {
        KeyStroke keyStroke = screen.readInput();

        if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
            previousMonth();
        } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
            nextMonth();
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            close();
        }
    }

    private void previousMonth() {
        currentMonth--;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        loadCalendarItems();
    }

    private void nextMonth() {
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        loadCalendarItems();
    }

    private void loadCalendarItems() {
        try {
            calendarItems = calendarService.getItemsForMonth(currentYear, currentMonth, currentUser.getId());
        } catch (Exception e) {
            calendarItems = List.of();
        }
    }
}

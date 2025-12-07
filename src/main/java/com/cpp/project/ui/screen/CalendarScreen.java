package com.cpp.project.ui.screen;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.ui.core.UIScreen;
import com.cpp.project.ui.strategy.RenderingStrategy;
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

    /**
     * Strategy Pattern: Calendar Grid Rendering Strategy
     * Encapsulates the logic for rendering the monthly calendar grid
     */
    private static class CalendarGridRenderer implements RenderingStrategy {
        private final int year;
        private final int month;
        private final List<CalendarItemDTO> items;

        public CalendarGridRenderer(int year, int month, List<CalendarItemDTO> items) {
            this.year = year;
            this.month = month;
            this.items = items;
        }

        @Override
        public void render(TextGraphics graphics, int x, int y) {
            // Day headers
            String[] dayHeaders = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            graphics.setForegroundColor(TextColor.ANSI.CYAN);
            for (int i = 0; i < dayHeaders.length; i++) {
                graphics.putString(x + i * 5, y, dayHeaders[i]);
            }

            // Calculate calendar grid
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, 1);
            int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0=Sunday
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            int row = y + 1;
            int col = firstDayOfWeek;

            // Render each day
            for (int day = 1; day <= daysInMonth; day++) {
                renderDay(graphics, x, row, col, day);

                col++;
                if (col == 7) {
                    col = 0;
                    row++;
                }
            }

            // Legend
            graphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
            graphics.putString(x, row + 2, "Yellow: Today");
            graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            graphics.putString(x + 20, row + 2, "Green*: Has tasks");
        }

        private void renderDay(TextGraphics graphics, int startX, int row, int col, int day) {
            boolean hasTask = hasTasks(day);
            boolean isToday = isToday(day);

            // Choose color based on day state
            if (isToday) {
                graphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
            } else if (hasTask) {
                graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            } else {
                graphics.setForegroundColor(TextColor.ANSI.WHITE);
            }

            String dayStr = String.format("%2d", day);
            graphics.putString(startX + col * 5, row, dayStr + (hasTask ? "*" : " "));
        }

        private boolean hasTasks(int day) {
            return items.stream().anyMatch(item -> {
                Calendar itemCal = Calendar.getInstance();
                itemCal.setTime(item.getDate());
                return itemCal.get(Calendar.YEAR) == year &&
                        itemCal.get(Calendar.MONTH) == month - 1 &&
                        itemCal.get(Calendar.DAY_OF_MONTH) == day;
            });
        }

        private boolean isToday(int day) {
            Calendar today = Calendar.getInstance();
            return today.get(Calendar.YEAR) == year &&
                    today.get(Calendar.MONTH) == month - 1 &&
                    today.get(Calendar.DAY_OF_MONTH) == day;
        }
    }

    /**
     * Strategy Pattern: Task List Rendering Strategy
     * Encapsulates the logic for rendering the task list
     */
    private static class TaskListRenderer implements RenderingStrategy {
        private static final int MAX_TASKS = 8;
        private final List<CalendarItemDTO> items;
        private final SimpleDateFormat dateFormat;

        public TaskListRenderer(List<CalendarItemDTO> items, SimpleDateFormat dateFormat) {
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
}

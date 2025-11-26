package com.cpp.project.ui.screen;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.service.CalendarService;
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
 * Calendar screen for viewing monthly calendar with tasks
 * Shows all deadlines from both courses and to-do lists
 */
public class CalendarScreen {
    private final Screen screen;
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

    public CalendarScreen(Screen screen, UserDTO currentUser, CalendarService calendarService) {
        this.screen = screen;
        this.currentUser = currentUser;
        this.calendarService = calendarService;

        // Initialize to current month
        Calendar cal = Calendar.getInstance();
        this.currentYear = cal.get(Calendar.YEAR);
        this.currentMonth = cal.get(Calendar.MONTH) + 1;
    }

    public void display() throws IOException {
        loadCalendarItems();
        boolean running = true;

        while (running) {
            screen.clear();
            render();
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
                previousMonth();
            } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
                nextMonth();
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                running = false;
            }
        }
    }

    private void render() {
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

        // Calendar grid
        renderCalendarGrid(graphics);

        // Tasks list
        renderTasksList(graphics);
    }

    private void renderCalendarGrid(TextGraphics graphics) {
        int startY = 7;
        int startX = 5;

        // Day headers
        String[] dayHeaders = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        for (int i = 0; i < dayHeaders.length; i++) {
            graphics.putString(startX + i * 5, startY, dayHeaders[i]);
        }

        // Calculate calendar grid
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth - 1, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0=Sunday
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int row = startY + 1;
        int col = firstDayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(currentYear, currentMonth - 1, day);

            // Check if this day has tasks
            int finalDay = day;
            boolean hasTask = calendarItems.stream().anyMatch(item -> {
                Calendar itemCal = Calendar.getInstance();
                itemCal.setTime(item.getDate());
                return itemCal.get(Calendar.DAY_OF_MONTH) == finalDay;
            });

            // Highlight current day
            Calendar today = Calendar.getInstance();
            boolean isToday = today.get(Calendar.YEAR) == currentYear &&
                    today.get(Calendar.MONTH) == currentMonth - 1 &&
                    today.get(Calendar.DAY_OF_MONTH) == day;

            if (isToday) {
                graphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
            } else if (hasTask) {
                graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            } else {
                graphics.setForegroundColor(TextColor.ANSI.WHITE);
            }

            String dayStr = String.format("%2d", day);
            graphics.putString(startX + col * 5, row, dayStr + (hasTask ? "*" : " "));

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }

        // Legend
        graphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        graphics.putString(startX, row + 2, "Yellow: Today");
        graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        graphics.putString(startX + 20, row + 2, "Green*: Has tasks");
    }

    private void renderTasksList(TextGraphics graphics) {
        int startY = 20;

        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        graphics.putString(3, startY, "Tasks this month:");

        if (calendarItems.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(5, startY + 2, "No tasks scheduled for this month");
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            int y = startY + 2;
            int maxTasks = 8; // Limit display to avoid overflow

            for (int i = 0; i < Math.min(calendarItems.size(), maxTasks); i++) {
                CalendarItemDTO item = calendarItems.get(i);

                // Format: Date | Type | Title | Status
                String taskLine = String.format("%s | %-10s | %-25s | %s",
                        dateFormat.format(item.getDate()),
                        item.getSourceType(),
                        truncate(item.getTitle(), 25),
                        item.getStatus());

                // Color code by source type
                if ("COURSE".equals(item.getSourceType())) {
                    graphics.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT);
                } else {
                    graphics.setForegroundColor(TextColor.ANSI.MAGENTA_BRIGHT);
                }

                graphics.putString(5, y, taskLine);
                y++;
            }

            if (calendarItems.size() > maxTasks) {
                graphics.setForegroundColor(TextColor.ANSI.YELLOW);
                graphics.putString(5, y + 1, "... and " + (calendarItems.size() - maxTasks) + " more tasks");
            }

            // Legend
            graphics.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT);
            graphics.putString(5, y + 3, "Blue: Course tasks");
            graphics.setForegroundColor(TextColor.ANSI.MAGENTA_BRIGHT);
            graphics.putString(30, y + 3, "Magenta: To-Do tasks");
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

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}

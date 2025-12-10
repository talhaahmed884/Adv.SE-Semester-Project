package com.cpp.project.ui.component.calendar;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.ui.strategy.RenderingStrategy;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Strategy Pattern: Calendar Grid Rendering Strategy
 * Encapsulates the logic for rendering the monthly calendar grid
 */
public class CalendarGridRenderer implements RenderingStrategy {
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

        // Calculate calendar grid using LocalDate (UTC-based)
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // 0=Sunday, 6=Saturday
        int daysInMonth = firstDay.lengthOfMonth();

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
            // Convert Instant to UTC ZonedDateTime for date comparison
            ZonedDateTime itemDateTime = item.getDate().atZone(ZoneId.of("UTC"));
            return itemDateTime.getYear() == year &&
                    itemDateTime.getMonthValue() == month &&
                    itemDateTime.getDayOfMonth() == day;
        });
    }

    private boolean isToday(int day) {
        // Get current date in UTC
        ZonedDateTime todayUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        return todayUTC.getYear() == year &&
                todayUTC.getMonthValue() == month &&
                todayUTC.getDayOfMonth() == day;
    }
}

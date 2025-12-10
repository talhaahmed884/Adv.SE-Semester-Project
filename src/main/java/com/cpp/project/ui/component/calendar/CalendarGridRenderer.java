package com.cpp.project.ui.component.calendar;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.ui.strategy.RenderingStrategy;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.util.Calendar;
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

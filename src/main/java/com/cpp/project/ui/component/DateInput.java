package com.cpp.project.ui.component;

import com.cpp.project.ui.strategy.DateValidationStrategy;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.time.DateTimeException;
import java.time.Instant;

/**
 * Component Pattern: Date and Time input component with built-in validation
 * Composed of five separate fields: year, month, day, hour, minute
 */
public class DateInput extends AbstractComponent {
    private final String label;
    private StringBuilder year;
    private StringBuilder month;
    private StringBuilder day;
    private StringBuilder hour;
    private StringBuilder minute;
    private int focusedField; // 0=year, 1=month, 2=day, 3=hour, 4=minute
    private String errorMessage;

    public DateInput(String label) {
        super(5);
        this.label = label;
        this.year = new StringBuilder();
        this.month = new StringBuilder();
        this.day = new StringBuilder();
        this.hour = new StringBuilder();
        this.minute = new StringBuilder();
        this.focusedField = 0;
        this.errorMessage = "";
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(x, y, label + " (use ← → to navigate):");

        // Year field
        TextColor yearColor = focused && focusedField == 0 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE;
        graphics.setForegroundColor(yearColor);
        String yearDisplay = !year.isEmpty() ? year.toString() : "YYYY";
        graphics.putString(x + 2, y + 1, "Year: " + yearDisplay + (focused && focusedField == 0 ? "_" : ""));

        // Month field
        TextColor monthColor = focused && focusedField == 1 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE;
        graphics.setForegroundColor(monthColor);
        String monthDisplay = !month.isEmpty() ? month.toString() : "MM";
        graphics.putString(x + 2, y + 2, "Month (1-12): " + monthDisplay + (focused && focusedField == 1 ? "_" : ""));

        // Day field
        TextColor dayColor = focused && focusedField == 2 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE;
        graphics.setForegroundColor(dayColor);
        String dayDisplay = !day.isEmpty() ? day.toString() : "DD";
        graphics.putString(x + 2, y + 3, "Day (1-31): " + dayDisplay + (focused && focusedField == 2 ? "_" : ""));

        // Hour field
        TextColor hourColor = focused && focusedField == 3 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE;
        graphics.setForegroundColor(hourColor);
        String hourDisplay = !hour.isEmpty() ? hour.toString() : "HH";
        graphics.putString(x + 2, y + 4, "Hour (0-23): " + hourDisplay + (focused && focusedField == 3 ? "_" : ""));

        // Minute field
        TextColor minuteColor = focused && focusedField == 4 ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.WHITE;
        graphics.setForegroundColor(minuteColor);
        String minuteDisplay = !minute.isEmpty() ? minute.toString() : "MM";
        graphics.putString(x + 2, y + 5, "Minute (0-59): " + minuteDisplay + (focused && focusedField == 4 ? "_" : ""));
    }

    @Override
    public boolean handleInput(KeyStroke keyStroke) {
        if (!focused) {
            return false;
        }

        if (keyStroke.getKeyType() == KeyType.Character) {
            char c = keyStroke.getCharacter();
            if (Character.isDigit(c)) {
                getCurrentField().append(c);
                return true;
            }
        } else if (keyStroke.getKeyType() == KeyType.Backspace) {
            StringBuilder current = getCurrentField();
            if (!current.isEmpty()) {
                current.deleteCharAt(current.length() - 1);
                return true;
            }
        } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
            // Move to next sub-field (year → month → day → hour → minute → wraps to year)
            focusedField = (focusedField + 1) % 5;
            return true;
        } else if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
            // Move to previous sub-field (minute → hour → day → month → year → wraps to minute)
            focusedField = (focusedField - 1 + 5) % 5;
            return true;
        }

        // Don't consume Tab - let Form handle it to move to next field
        return false;
    }

    private StringBuilder getCurrentField() {
        return switch (focusedField) {
            case 1 -> month;
            case 2 -> day;
            case 3 -> hour;
            case 4 -> minute;
            default -> year;
        };
    }

    /**
     * Validate and return Instant object (local timezone converted to UTC), or null if invalid
     */
    public Instant getDate() {
        errorMessage = "";

        if (year.isEmpty() || month.isEmpty() || day.isEmpty()) {
            errorMessage = "Invalid date/time format";
            return null;
        }

        try {
            int y = Integer.parseInt(year.toString());
            int m = Integer.parseInt(month.toString());
            int d = Integer.parseInt(day.toString());
            int h = hour.isEmpty() ? 0 : Integer.parseInt(hour.toString());
            int min = minute.isEmpty() ? 0 : Integer.parseInt(minute.toString());

            if (m < 1 || m > 12) {
                errorMessage = "Month must be between 1 and 12";
                return null;
            }
            if (d < 1 || d > 31) {
                errorMessage = "Day must be between 1 and 31";
                return null;
            }
            if (y < 2000 || y > 2100) {
                errorMessage = "Year must be between 2000 and 2100";
                return null;
            }
            if (h < 0 || h > 23) {
                errorMessage = "Hour must be between 0 and 23";
                return null;
            }
            if (min < 0 || min > 59) {
                errorMessage = "Minute must be between 0 and 59";
                return null;
            }

            return DateValidationStrategy.createDateTime(y, m, d, h, min);
        } catch (NumberFormatException e) {
            errorMessage = "Invalid date/time format";
            return null;
        } catch (DateTimeException e) {
            errorMessage = "Invalid date (e.g., Feb 31 doesn't exist)";
            return null;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isEmpty() {
        return year.isEmpty() && month.isEmpty() && day.isEmpty() && hour.isEmpty() && minute.isEmpty();
    }

    public void clear() {
        year = new StringBuilder();
        month = new StringBuilder();
        day = new StringBuilder();
        hour = new StringBuilder();
        minute = new StringBuilder();
        focusedField = 0;
        errorMessage = "";
    }

    /**
     * Set the date fields from an Instant
     * Converts UTC instant to local timezone for display
     *
     * @param instant The instant to set
     */
    public void setDate(Instant instant) {
        if (instant == null) {
            clear();
            return;
        }

        java.time.ZonedDateTime zonedDateTime = instant.atZone(java.time.ZoneId.systemDefault());

        year = new StringBuilder(String.valueOf(zonedDateTime.getYear()));
        month = new StringBuilder(String.valueOf(zonedDateTime.getMonthValue()));
        day = new StringBuilder(String.valueOf(zonedDateTime.getDayOfMonth()));
        hour = new StringBuilder(String.valueOf(zonedDateTime.getHour()));
        minute = new StringBuilder(String.valueOf(zonedDateTime.getMinute()));

        focusedField = 0;
        errorMessage = "";
    }
}

package com.cpp.project.ui.component;

import com.cpp.project.ui.strategy.DateValidationStrategy;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.Date;

/**
 * Component Pattern: Date input component with built-in validation
 * Composed of three separate fields: year, month, day
 */
public class DateInput extends AbstractComponent {
    private final String label;
    private StringBuilder year;
    private StringBuilder month;
    private StringBuilder day;
    private int focusedField; // 0=year, 1=month, 2=day
    private String errorMessage;

    public DateInput(String label) {
        super(3);
        this.label = label;
        this.year = new StringBuilder();
        this.month = new StringBuilder();
        this.day = new StringBuilder();
        this.focusedField = 0;
        this.errorMessage = "";
    }

    @Override
    public void render(TextGraphics graphics, int x, int y) {
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(x, y, label + ":");

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
        } else if (keyStroke.getKeyType() == KeyType.Tab) {
            focusedField = (focusedField + 1) % 3;
            return true;
        }

        return false;
    }

    private StringBuilder getCurrentField() {
        return switch (focusedField) {
            case 1 -> month;
            case 2 -> day;
            default -> year;
        };
    }

    /**
     * Validate and return Date object, or null if invalid
     */
    public Date getDate() {
        errorMessage = "";

        if (year.isEmpty() || month.isEmpty() || day.isEmpty()) {
            return null;
        }

        try {
            int y = Integer.parseInt(year.toString());
            int m = Integer.parseInt(month.toString());
            int d = Integer.parseInt(day.toString());

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

            return DateValidationStrategy.createDate(y, m, d);
        } catch (NumberFormatException e) {
            errorMessage = "Invalid date format";
            return null;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isEmpty() {
        return year.isEmpty() && month.isEmpty() && day.isEmpty();
    }

    public void clear() {
        year = new StringBuilder();
        month = new StringBuilder();
        day = new StringBuilder();
        focusedField = 0;
        errorMessage = "";
    }
}

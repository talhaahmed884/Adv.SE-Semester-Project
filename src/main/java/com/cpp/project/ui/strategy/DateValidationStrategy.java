package com.cpp.project.ui.strategy;

import java.util.Calendar;
import java.util.Date;

/**
 * Validates date fields
 */
public class DateValidationStrategy implements ValidationStrategy {
    /**
     * Validate individual date components
     */
    public static String validateDate(int year, int month, int day) {
        if (month < 1 || month > 12) {
            return "Month must be between 1 and 12";
        }
        if (day < 1 || day > 31) {
            return "Day must be between 1 and 31";
        }
        if (year < 2000 || year > 2100) {
            return "Year must be between 2000 and 2100";
        }
        return null;
    }

    /**
     * Create Date from components
     */
    public static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return cal.getTime();
    }

    @Override
    public String validate(String input) {
        // This is used for Date objects converted to strings
        // The actual validation is in DateInput component
        return null;
    }
}

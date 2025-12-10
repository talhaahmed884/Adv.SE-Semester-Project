package com.cpp.project.ui.strategy;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Validates date fields and creates UTC-based Instant objects
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
     * Create Instant from components (interpreted as local timezone midnight, converted to UTC)
     *
     * @param year  Year (e.g., 2024)
     * @param month Month (1-12)
     * @param day   Day (1-31)
     * @return Instant representing the date at midnight in local timezone, converted to UTC
     */
    public static Instant createDate(int year, int month, int day) {
        // Create ZonedDateTime at midnight in user's local timezone
        ZonedDateTime localDateTime = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault());
        // Convert to Instant (automatically converts to UTC)
        return localDateTime.toInstant();
    }

    @Override
    public String validate(String input) {
        // This is used for Date objects converted to strings
        // The actual validation is in DateInput component
        return null;
    }
}

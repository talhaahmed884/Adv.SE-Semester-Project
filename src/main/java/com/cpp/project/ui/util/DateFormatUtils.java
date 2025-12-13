package com.cpp.project.ui.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Date Formatting Utilities for consistent date/time display across UI
 * <p>
 * This class centralizes all date formatting patterns and provides
 * utility methods for formatting dates consistently throughout the application.
 * <p>
 * Supports multiple date/time types: Instant, LocalDateTime, LocalDate
 */
public final class DateFormatUtils {

    /**
     * Standard format for deadlines: "MMM dd, yyyy hh:mm a"
     * Example: "Dec 25, 2023 03:30 PM"
     */
    public static final DateTimeFormatter DEADLINE_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd, yyyy hh:mm a")
            .withZone(ZoneId.systemDefault());

    /**
     * Local date time format (no zone): "MMM dd, yyyy hh:mm a"
     * Example: "Dec 25, 2023 03:30 PM"
     */
    public static final DateTimeFormatter LOCAL_DATETIME_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd, yyyy hh:mm a");

    /**
     * Detailed format for timestamps with seconds: "MMM dd, yyyy hh:mm:ss a"
     * Example: "Dec 25, 2023 03:30:45 PM"
     */
    public static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd, yyyy hh:mm:ss a")
            .withZone(ZoneId.systemDefault());

    /**
     * Local timestamp format with seconds (no zone): "MMM dd, yyyy hh:mm:ss a"
     * Example: "Dec 25, 2023 03:30:45 PM"
     */
    public static final DateTimeFormatter LOCAL_TIMESTAMP_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd, yyyy hh:mm:ss a");

    /**
     * Short date format without time: "MMM dd, yyyy"
     * Example: "Dec 25, 2023"
     */
    public static final DateTimeFormatter DATE_ONLY_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd, yyyy")
            .withZone(ZoneId.systemDefault());

    /**
     * Local date only format (no zone): "MMM dd, yyyy"
     * Example: "Dec 25, 2023"
     */
    public static final DateTimeFormatter LOCAL_DATE_ONLY_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd, yyyy");

    /**
     * Full month and year format: "MMMM dd, yyyy"
     * Example: "December 25, 2023"
     */
    public static final DateTimeFormatter FULL_DATE_FORMAT = DateTimeFormatter
            .ofPattern("MMMM dd, yyyy");

    /**
     * Time only format: "hh:mm a"
     * Example: "03:30 PM"
     */
    public static final DateTimeFormatter TIME_ONLY_FORMAT = DateTimeFormatter
            .ofPattern("hh:mm a")
            .withZone(ZoneId.systemDefault());

    // ========== Formatting Helper Methods for Instant ==========

    // Prevent instantiation
    private DateFormatUtils() {
        throw new AssertionError("DateFormatUtils should not be instantiated");
    }

    /**
     * Format deadline with fallback message
     *
     * @param deadline Deadline instant (can be null)
     * @return Formatted deadline or "No deadline" if null
     */
    public static String formatDeadline(Instant deadline) {
        return deadline != null ? DEADLINE_FORMAT.format(deadline) : "No deadline";
    }

    /**
     * Format timestamp with fallback message
     *
     * @param timestamp Timestamp instant (can be null)
     * @return Formatted timestamp or empty string if null
     */
    public static String formatTimestamp(Instant timestamp) {
        return timestamp != null ? TIMESTAMP_FORMAT.format(timestamp) : "";
    }

    /**
     * Format date only (without time)
     *
     * @param instant Instant to format (can be null)
     * @return Formatted date or empty string if null
     */
    public static String formatDateOnly(Instant instant) {
        return instant != null ? DATE_ONLY_FORMAT.format(instant) : "";
    }

    /**
     * Format time only (without date)
     *
     * @param instant Instant to format (can be null)
     * @return Formatted time or empty string if null
     */
    public static String formatTimeOnly(Instant instant) {
        return instant != null ? TIME_ONLY_FORMAT.format(instant) : "";
    }

    // ========== Formatting Helper Methods for LocalDateTime ==========

    /**
     * Format created/updated timestamp with label
     *
     * @param label     Label for the timestamp (e.g., "Created", "Updated")
     * @param timestamp Timestamp instant (can be null)
     * @return Formatted string like "Created: Dec 25, 2023 03:30:45 PM" or empty if null
     */
    public static String formatTimestampWithLabel(String label, Instant timestamp) {
        return timestamp != null ? label + ": " + TIMESTAMP_FORMAT.format(timestamp) : "";
    }

    /**
     * Format LocalDateTime as deadline
     *
     * @param deadline LocalDateTime deadline (can be null)
     * @return Formatted deadline or "No deadline" if null
     */
    public static String formatDeadline(LocalDateTime deadline) {
        return deadline != null ? LOCAL_DATETIME_FORMAT.format(deadline) : "No deadline";
    }

    /**
     * Format LocalDateTime as timestamp
     *
     * @param timestamp LocalDateTime timestamp (can be null)
     * @return Formatted timestamp or empty string if null
     */
    public static String formatTimestamp(LocalDateTime timestamp) {
        return timestamp != null ? LOCAL_TIMESTAMP_FORMAT.format(timestamp) : "";
    }

    /**
     * Format LocalDateTime as date only (without time)
     *
     * @param dateTime LocalDateTime to format (can be null)
     * @return Formatted date or empty string if null
     */
    public static String formatDateOnly(LocalDateTime dateTime) {
        return dateTime != null ? LOCAL_DATE_ONLY_FORMAT.format(dateTime) : "";
    }

    /**
     * Format LocalDateTime as full date (e.g., "December 25, 2023")
     *
     * @param dateTime LocalDateTime to format (can be null)
     * @return Formatted full date or empty string if null
     */
    public static String formatFullDate(LocalDateTime dateTime) {
        return dateTime != null ? FULL_DATE_FORMAT.format(dateTime) : "";
    }

    // ========== Formatting Helper Methods for LocalDate ==========

    /**
     * Format created/updated timestamp with label
     *
     * @param label     Label for the timestamp (e.g., "Created", "Updated")
     * @param timestamp LocalDateTime timestamp (can be null)
     * @return Formatted string like "Created: Dec 25, 2023 03:30:45 PM" or empty if null
     */
    public static String formatTimestampWithLabel(String label, LocalDateTime timestamp) {
        return timestamp != null ? label + ": " + LOCAL_TIMESTAMP_FORMAT.format(timestamp) : "";
    }

    /**
     * Format LocalDate as date only
     *
     * @param date LocalDate to format (can be null)
     * @return Formatted date or empty string if null
     */
    public static String formatDateOnly(LocalDate date) {
        return date != null ? LOCAL_DATE_ONLY_FORMAT.format(date) : "";
    }

    /**
     * Format LocalDate as full date (e.g., "December 25, 2023")
     *
     * @param date LocalDate to format (can be null)
     * @return Formatted full date or empty string if null
     */
    public static String formatFullDate(LocalDate date) {
        return date != null ? FULL_DATE_FORMAT.format(date) : "";
    }
}

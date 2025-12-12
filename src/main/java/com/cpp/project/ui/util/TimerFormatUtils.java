package com.cpp.project.ui.util;

import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.entity.TimerStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting timer-related data for display in the Terminal UI
 */
public class TimerFormatUtils {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter
            .ofPattern("MMM dd, h:mm a")
            .withZone(ZoneId.systemDefault());

    /**
     * Format duration in milliseconds to human-readable string
     * Examples: "2h 15m 30s", "45m 12s", "23s"
     *
     * @param millis Duration in milliseconds
     * @return Formatted duration string
     */
    public static String formatDuration(long millis) {
        if (millis <= 0) {
            return "0s";
        }

        Duration duration = Duration.ofMillis(millis);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder result = new StringBuilder();

        if (hours > 0) {
            result.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0) {
            result.append(minutes).append("m ");
        }
        if (seconds > 0 || (hours == 0 && minutes == 0)) {
            result.append(seconds).append("s");
        }

        return result.toString().trim();
    }

    /**
     * Format instant to human-readable timestamp
     * Example: "Dec 12, 2:45 PM"
     *
     * @param instant Instant to format
     * @return Formatted timestamp string
     */
    public static String formatTimestamp(Instant instant) {
        if (instant == null) {
            return "N/A";
        }
        return TIMESTAMP_FORMATTER.format(instant);
    }

    /**
     * Get timer status display string
     *
     * @param timer Timer DTO
     * @return "Running" or "Stopped"
     */
    public static String formatTimerStatus(TimerDTO timer) {
        if (timer == null) {
            return "N/A";
        }
        return timer.getStatus() == TimerStatus.RUNNING ? "Running" : "Stopped";
    }

    /**
     * Calculate elapsed time for a running timer
     *
     * @param startTime Start time of the timer
     * @return Elapsed time in milliseconds
     */
    public static long calculateElapsedMillis(Instant startTime) {
        if (startTime == null) {
            return 0;
        }
        return Duration.between(startTime, Instant.now()).toMillis();
    }

    /**
     * Format timer session for display in session history
     * Examples:
     * - "Session 12: 25m 47s (Dec 12, 2:45 PM - Running)"
     * - "Session 11: 45m 23s (Dec 11, 4:30 PM - 5:15 PM)"
     *
     * @param sessionNumber Session number
     * @param timer         Timer DTO
     * @return Formatted session string
     */
    public static String formatTimerSession(int sessionNumber, TimerDTO timer) {
        if (timer == null) {
            return "N/A";
        }

        StringBuilder result = new StringBuilder();
        result.append("Session ").append(sessionNumber).append(": ");

        // Duration
        if (timer.getStatus() == TimerStatus.RUNNING) {
            long elapsed = calculateElapsedMillis(timer.getStartTime());
            result.append(formatDuration(elapsed));
        } else {
            result.append(formatDuration(timer.getDurationMillis()));
        }

        result.append(" (");

        // Start time
        result.append(formatTimestamp(timer.getStartTime()));

        // End time or "Running"
        if (timer.getStatus() == TimerStatus.RUNNING) {
            result.append(" - Running");
        } else {
            result.append(" - ").append(formatTimestamp(timer.getEndTime()));
        }

        result.append(")");

        return result.toString();
    }
}

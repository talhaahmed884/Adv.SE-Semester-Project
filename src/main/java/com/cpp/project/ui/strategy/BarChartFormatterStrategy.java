package com.cpp.project.ui.strategy;

/**
 * Strategy for formatting percentage values as ASCII bar charts
 * Strategy Pattern - Encapsulates bar chart formatting algorithm
 * <p>
 * Example output: [████████░░░░░░░░░░░░] 40%
 */
public class BarChartFormatterStrategy {
    private static final int BAR_WIDTH = 20;
    private static final char FILLED_CHAR = '█';
    private static final char EMPTY_CHAR = '░';

    /**
     * Format a percentage as an ASCII bar chart
     *
     * @param percentage Value between 0-100
     * @param label      Optional label to display before the bar
     * @return Formatted bar chart string
     */
    public String format(double percentage, String label) {
        // Clamp percentage to 0-100
        percentage = Math.max(0, Math.min(100, percentage));

        // Calculate filled and empty sections
        int filledCount = (int) Math.round((percentage / 100.0) * BAR_WIDTH);
        int emptyCount = BAR_WIDTH - filledCount;

        // Build the bar
        StringBuilder bar = new StringBuilder();

        // Add label if provided
        if (label != null && !label.isEmpty()) {
            bar.append(String.format("%-20s ", label));  // Left-align, 20 chars wide
        }

        // Add bar chart
        bar.append('[');
        for (int i = 0; i < filledCount; i++) {
            bar.append(FILLED_CHAR);
        }
        for (int i = 0; i < emptyCount; i++) {
            bar.append(EMPTY_CHAR);
        }
        bar.append(']');

        // Add percentage value
        bar.append(String.format(" %.1f%%", percentage));

        return bar.toString();
    }

    /**
     * Format a percentage as an ASCII bar chart with time display
     *
     * @param percentage Value between 0-100
     * @param label      Label to display before the bar
     * @param timeStr    Time string to display after percentage (e.g., "2h 30m")
     * @return Formatted bar chart string with time
     */
    public String formatWithTime(double percentage, String label, String timeStr) {
        String baseFormat = format(percentage, label);
        return baseFormat + " (" + timeStr + ")";
    }
}

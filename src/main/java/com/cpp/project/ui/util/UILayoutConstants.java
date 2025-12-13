package com.cpp.project.ui.util;

import com.googlecode.lanterna.TerminalSize;

/**
 * UI Layout Constants for consistent positioning across UI states
 * <p>
 * This class centralizes all magic numbers used for UI layout,
 * making the code more maintainable and self-documenting.
 */
public final class UILayoutConstants {

    // ========== Horizontal Positions ==========

    /**
     * Standard left margin for content
     */
    public static final int LEFT_MARGIN = 3;

    /**
     * Left position for forms and input fields
     */
    public static final int FORM_LEFT = 5;

    // ========== Vertical Positions ==========

    /**
     * Row for screen title
     */
    public static final int TITLE_ROW = 1;

    /**
     * Row for instructions
     */
    public static final int INSTRUCTIONS_ROW = 3;

    /**
     * Starting row for list content
     */
    public static final int CONTENT_START_ROW = 5;

    /**
     * Starting row for forms
     */
    public static final int FORM_START_ROW = 5;

    /**
     * Row for additional notes or hints
     */
    public static final int NOTE_ROW = 14;

    /**
     * Row for list/content rendering
     */
    public static final int LIST_ROW = 8;

    /**
     * Row for additional information section
     */
    public static final int INFO_SECTION_ROW = 6;

    public static final int BOTTOM_MARGIN = 2;

    public static final int FORM_LEFT_MARGIN = 5;

    // ========== Calculated Positions ==========

    // Prevent instantiation
    private UILayoutConstants() {
        throw new AssertionError("UILayoutConstants should not be instantiated");
    }

    /**
     * Calculate message row position (always 2 rows from bottom)
     *
     * @param size Terminal size
     * @return Row position for messages
     */
    public static int messageRow(TerminalSize size) {
        return size.getRows() - 2;
    }

    /**
     * Calculate centered X position for text
     *
     * @param size       Terminal size
     * @param textLength Length of text to center
     * @return Centered X position
     */
    public static int centerX(TerminalSize size, int textLength) {
        return (size.getColumns() - textLength) / 2;
    }
}

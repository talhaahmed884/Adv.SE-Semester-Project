package com.cpp.project.ui.mediator;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.ui.core.ScreenMediator;

import java.util.List;
import java.util.UUID;

/**
 * Mediator interface for Calendar screen-state interactions
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Coordinates communication between states
 * - Facade Pattern: Simplifies access to services and data
 */
public interface CalendarMediator extends ScreenMediator {

    // ========== Facade: Data Access Methods ==========

    /**
     * Get calendar items for a specific month
     *
     * @param year       The year
     * @param month      The month (1-12)
     * @param userId     The user ID
     * @param timezoneId The user's timezone ID (e.g., "America/New_York")
     * @return Fresh list of calendar items from service
     */
    List<CalendarItemDTO> getItemsForMonth(int year, int month, UUID userId, String timezoneId);

    /**
     * Get current year being displayed
     *
     * @return Current year
     */
    int getCurrentYear();

    /**
     * Get current month being displayed
     *
     * @return Current month (1-12)
     */
    int getCurrentMonth();

    /**
     * Get month names for display
     *
     * @return Array of month names
     */
    String[] getMonthNames();

    // ========== Mediator: Action Notification Methods ==========

    /**
     * Called when user wants to navigate to previous month
     * Mediator decides next state (usually refresh with previous month)
     */
    void onPreviousMonth();

    /**
     * Called when user wants to navigate to next month
     * Mediator decides next state (usually refresh with next month)
     */
    void onNextMonth();

    /**
     * Called when user wants to return to main menu
     * Mediator closes the calendar screen
     */
    void onReturnToMainMenu();
}

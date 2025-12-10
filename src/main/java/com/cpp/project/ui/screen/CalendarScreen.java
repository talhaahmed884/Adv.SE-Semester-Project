package com.cpp.project.ui.screen;

import com.cpp.project.calendar.dto.CalendarItemDTO;
import com.cpp.project.calendar.service.CalendarService;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.core.StatefulScreen;
import com.cpp.project.ui.mediator.CalendarMediator;
import com.cpp.project.ui.state.calendar.MonthViewState;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.screen.Screen;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Calendar Screen implementing Mediator pattern
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Coordinates all state interactions and transitions
 * - Facade Pattern: Provides simple interface for states to access data
 * - Factory Method Pattern: Creates states through factory methods
 * - State Pattern: Delegates UI behavior to state objects
 * - Strategy Pattern: Rendering strategies cached in states for performance
 * <p>
 * Responsibilities:
 * - Owns the calendar data (year, month)
 * - Coordinates state transitions
 * - Provides data access to states via facade methods
 * - Handles state action notifications
 */
public class CalendarScreen extends StatefulScreen implements CalendarMediator {
    private final UserDTO currentUser;
    private final CalendarService calendarService;
    private final String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private int currentYear;
    private int currentMonth; // 1-12

    public CalendarScreen(Screen screen, UserDTO currentUser, CalendarService calendarService) {
        super(screen);
        this.currentUser = currentUser;
        this.calendarService = calendarService;

        // Initialize to current month
        Calendar cal = Calendar.getInstance();
        this.currentYear = cal.get(Calendar.YEAR);
        this.currentMonth = cal.get(Calendar.MONTH) + 1;

        // Start with month view state
        this.currentState = createMonthViewState();
        this.currentState.onEnter();
    }

    // ========== Facade Pattern: Simplified Data Access ==========

    @Override
    public List<CalendarItemDTO> getItemsForMonth(int year, int month, UUID userId, String timezoneId) {
        // Always fetch fresh from service
        try {
            return calendarService.getItemsForMonth(year, month, currentUser.getId(), timezoneId);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public int getCurrentYear() {
        return currentYear;
    }

    @Override
    public int getCurrentMonth() {
        return currentMonth;
    }

    @Override
    public String[] getMonthNames() {
        return monthNames;
    }

    // ========== Mediator Pattern: Action Handlers ==========

    @Override
    public void onPreviousMonth() {
        // Update month data
        currentMonth--;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }

        // Refresh the current state with new data
        if (currentState instanceof MonthViewState) {
            ((MonthViewState) currentState).updateMonth();
        }
    }

    @Override
    public void onNextMonth() {
        // Update month data
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }

        // Refresh the current state with new data
        if (currentState instanceof MonthViewState) {
            ((MonthViewState) currentState).updateMonth();
        }
    }

    @Override
    public void onReturnToMainMenu() {
        // User pressed ESC, close the calendar screen
        close();
    }

    // ========== ScreenMediator: Core Methods ==========

    @Override
    public void transitionTo(ScreenState newState) {
        transitionToState(newState);
    }

    @Override
    public void closeScreen() {
        close();
    }

    // ========== Factory Method Pattern: State Creation ==========

    /**
     * Factory method to create month view state
     *
     * @return New month view state
     */
    private MonthViewState createMonthViewState() {
        return new MonthViewState(this);
    }
}

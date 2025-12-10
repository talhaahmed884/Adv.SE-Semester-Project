package com.cpp.project.calendar.service;

import com.cpp.project.calendar.dto.CalendarItemDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Calendar operations
 */
public interface CalendarService {
    /**
     * Get all calendar items (tasks with deadlines) for a specific month and user
     * Aggregates tasks from both Courses and ToDoLists
     *
     * @param year       The year (in user's timezone)
     * @param month      The month (1-12, in user's timezone)
     * @param userId     The user ID
     * @param timezoneId The user's timezone ID (e.g., "America/New_York", "Asia/Tokyo")
     * @return List of calendar items for the specified month
     */
    List<CalendarItemDTO> getItemsForMonth(int year, int month, UUID userId, String timezoneId);
}

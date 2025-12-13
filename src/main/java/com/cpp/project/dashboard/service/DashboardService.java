package com.cpp.project.dashboard.service;

import com.cpp.project.dashboard.dto.DashboardSummaryDTO;

import java.util.UUID;

/**
 * Service interface for Dashboard operations
 * Facade Pattern - Aggregates data from multiple services (Course, Timer, ToDoList)
 */
public interface DashboardService {
    /**
     * Get dashboard summary for a user
     * Includes:
     * - Course study time distribution (for Neglect Detector widget)
     * - Upcoming tasks in next 7 days (for Impending Doom widget)
     *
     * @param userId User ID
     * @return Dashboard summary DTO with aggregated data
     */
    DashboardSummaryDTO getDashboardSummary(UUID userId);
}

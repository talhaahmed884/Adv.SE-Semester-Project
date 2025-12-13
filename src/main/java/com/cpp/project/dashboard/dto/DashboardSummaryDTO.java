package com.cpp.project.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Dashboard Summary
 * Aggregates all dashboard data for a user
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private List<CourseStudyTimeDTO> courseStudyTimes = new ArrayList<>();
    private List<UpcomingTaskDTO> upcomingTasks = new ArrayList<>();
    private long totalStudyTimeMillis;  // Total study time across all courses

    // Builder Pattern
    public static DashboardSummaryDTOBuilder builder() {
        return new DashboardSummaryDTOBuilder();
    }
}

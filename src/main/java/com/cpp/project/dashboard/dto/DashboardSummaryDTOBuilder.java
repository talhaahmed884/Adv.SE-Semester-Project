package com.cpp.project.dashboard.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for DashboardSummaryDTO
 * Builder Pattern - Provides fluent interface for constructing DTOs
 */
public class DashboardSummaryDTOBuilder {
    private List<CourseStudyTimeDTO> courseStudyTimes = new ArrayList<>();
    private List<UpcomingTaskDTO> upcomingTasks = new ArrayList<>();
    private long totalStudyTimeMillis;

    public DashboardSummaryDTOBuilder courseStudyTimes(List<CourseStudyTimeDTO> courseStudyTimes) {
        this.courseStudyTimes = courseStudyTimes;
        return this;
    }

    public DashboardSummaryDTOBuilder upcomingTasks(List<UpcomingTaskDTO> upcomingTasks) {
        this.upcomingTasks = upcomingTasks;
        return this;
    }

    public DashboardSummaryDTOBuilder totalStudyTimeMillis(long totalStudyTimeMillis) {
        this.totalStudyTimeMillis = totalStudyTimeMillis;
        return this;
    }

    public DashboardSummaryDTO build() {
        return new DashboardSummaryDTO(courseStudyTimes, upcomingTasks, totalStudyTimeMillis);
    }
}

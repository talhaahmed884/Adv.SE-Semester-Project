package com.cpp.project.dashboard.dto;

import java.util.UUID;

/**
 * Builder for CourseStudyTimeDTO
 * Builder Pattern - Provides fluent interface for constructing DTOs
 */
public class CourseStudyTimeDTOBuilder {
    private UUID courseId;
    private String courseName;
    private String courseCode;
    private long totalMillis;
    private double percentage;

    public CourseStudyTimeDTOBuilder courseId(UUID courseId) {
        this.courseId = courseId;
        return this;
    }

    public CourseStudyTimeDTOBuilder courseName(String courseName) {
        this.courseName = courseName;
        return this;
    }

    public CourseStudyTimeDTOBuilder courseCode(String courseCode) {
        this.courseCode = courseCode;
        return this;
    }

    public CourseStudyTimeDTOBuilder totalMillis(long totalMillis) {
        this.totalMillis = totalMillis;
        return this;
    }

    public CourseStudyTimeDTOBuilder percentage(double percentage) {
        this.percentage = percentage;
        return this;
    }

    public CourseStudyTimeDTO build() {
        return new CourseStudyTimeDTO(courseId, courseName, courseCode, totalMillis, percentage);
    }
}

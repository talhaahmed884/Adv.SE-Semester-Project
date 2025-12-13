package com.cpp.project.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Data Transfer Object for Course Study Time statistics
 * Used by Neglect Detector widget to show time distribution per course
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseStudyTimeDTO {
    private UUID courseId;
    private String courseName;
    private String courseCode;
    private long totalMillis;        // Total time spent in milliseconds
    private double percentage;        // Percentage of total study time (0-100)

    // Builder Pattern
    public static CourseStudyTimeDTOBuilder builder() {
        return new CourseStudyTimeDTOBuilder();
    }
}

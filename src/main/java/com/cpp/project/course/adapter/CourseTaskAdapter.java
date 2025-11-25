package com.cpp.project.course.adapter;

import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.entity.CourseTask;

/**
 * Adapter Pattern - Converts between CourseTask entities and DTOs
 */
public class CourseTaskAdapter {
    /**
     * Convert CourseTask entity to DTO
     */
    public static CourseTaskDTO toDTO(CourseTask task) {
        if (task == null) {
            return null;
        }

        return CourseTaskDTO.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .progress(task.getProgress())
                .status(task.getStatus())
                .courseId(task.getCourse() != null ? task.getCourse().getId() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}

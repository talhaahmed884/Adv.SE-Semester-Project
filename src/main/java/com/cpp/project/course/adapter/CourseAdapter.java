package com.cpp.project.course.adapter;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.entity.Course;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter Pattern - Converts between Course entities and DTOs
 */
public class CourseAdapter {
    /**
     * Convert Course entity to DTO
     */
    public static CourseDTO toDTO(Course course) {
        if (course == null) {
            return null;
        }

        List<CourseTaskDTO> taskDTOs = course.getTasks() != null
                ? course.getTasks().stream()
                .map(CourseTaskAdapter::toDTO)
                .collect(Collectors.toList())
                : List.of();

        return CourseDTO.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .userId(course.getUserId())
                .progress(course.progress())
                .tasks(taskDTOs)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    /**
     * Convert Course entity to DTO without tasks (for list views)
     */
    public static CourseDTO toDTOWithoutTasks(Course course) {
        if (course == null) {
            return null;
        }

        return CourseDTO.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .userId(course.getUserId())
                .progress(course.progress())
                .tasks(List.of())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}

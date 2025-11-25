package com.cpp.project.course.dto;

import com.cpp.project.common.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Data Transfer Object for CourseTask
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseTaskDTO {
    private UUID id;
    private String name;
    private String description;
    private Date deadline;
    private int progress;
    private TaskStatus status;
    private UUID courseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder Pattern
    public static CourseTaskDTOBuilder builder() {
        return new CourseTaskDTOBuilder();
    }
}

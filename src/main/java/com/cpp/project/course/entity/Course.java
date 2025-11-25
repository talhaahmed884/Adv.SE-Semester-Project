package com.cpp.project.course.entity;

import com.cpp.project.common.entity.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Course entity
 * Represents a course that contains multiple tasks
 */
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
public class Course {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CourseTask> tasks = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Builder Pattern
    public static CourseBuilder builder() {
        return new CourseBuilder();
    }

    /**
     * Calculate overall progress of the course based on tasks
     * Progress is calculated as average of all task progress values
     *
     * @return Progress percentage (0-100)
     */
    public int progress() {
        if (tasks == null || tasks.isEmpty()) {
            return 0;
        }

        int totalProgress = tasks.stream()
                .mapToInt(CourseTask::getProgress)
                .sum();

        return totalProgress / tasks.size();
    }

    /**
     * Add a new task to the course
     *
     * @param name        Task name
     * @param deadline    Task deadline
     * @param description Task description
     * @return The created CourseTask
     */
    public CourseTask addTask(String name, Date deadline, String description) {
        CourseTask task = CourseTask.builder()
                .name(name)
                .description(description)
                .deadline(deadline)
                .progress(0)
                .status(TaskStatus.PENDING)
                .course(this)
                .build();

        tasks.add(task);
        return task;
    }
}

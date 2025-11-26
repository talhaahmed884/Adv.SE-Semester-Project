package com.cpp.project.course.entity;

import com.cpp.project.common.entity.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * CourseTask entity
 * Represents a task within a course
 */
@Entity
@Table(name = "course_tasks")
@Getter
@Setter
@NoArgsConstructor
public class CourseTask {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "deadline")
    private Date deadline;

    @Column(name = "progress", nullable = false)
    private int progress = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private TaskStatus status = TaskStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Builder Pattern
    public static CourseTaskBuilder builder() {
        return new CourseTaskBuilder();
    }

    /**
     * Update the progress of this task
     * Progress is clamped between 0 and 100
     * Automatically marks task as completed when progress reaches 100
     *
     * @param value Progress value (0-100)
     */
    public void updateProgress(int value) {
        // Clamp value between 0 and 100
        this.progress = Math.max(0, Math.min(100, value));

        // Update status based on progress
        if (this.progress == 0) {
            this.status = TaskStatus.PENDING;
        } else if (this.progress == 100) {
            this.status = TaskStatus.COMPLETED;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    /**
     * Mark this task as completed
     * Sets progress to 100 and status to COMPLETED
     */
    public void markComplete() {
        this.progress = 100;
        this.status = TaskStatus.COMPLETED;
    }
}

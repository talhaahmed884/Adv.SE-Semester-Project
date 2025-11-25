package com.cpp.project.todolist.entity;

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
 * ToDoListTask entity
 * Represents a task within a to-do list
 */
@Entity
@Table(name = "todo_list_tasks")
@Getter
@Setter
@NoArgsConstructor
public class ToDoListTask {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "todo_list_id", nullable = false, updatable = false, insertable = false)
    private UUID todoListId;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "deadline")
    private Date deadline;

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
    public static ToDoListTaskBuilder builder() {
        return new ToDoListTaskBuilder();
    }

    /**
     * Mark this task as completed
     * Sets status to COMPLETED
     */
    public void markComplete() {
        this.status = TaskStatus.COMPLETED;
    }
}

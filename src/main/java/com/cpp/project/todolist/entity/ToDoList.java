package com.cpp.project.todolist.entity;

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
 * ToDoList entity
 * Represents a to-do list that contains multiple tasks
 */
@Entity
@Table(name = "todo_lists")
@Getter
@Setter
@NoArgsConstructor
public class ToDoList {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "todoList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ToDoListTask> tasks = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Builder Pattern
    public static ToDoListBuilder builder() {
        return new ToDoListBuilder();
    }

    /**
     * Add a new task to the to-do list
     *
     * @param description Task description
     * @param deadline    Task deadline
     * @return The created ToDoListTask
     */
    public ToDoListTask addTask(String description, Date deadline) {
        ToDoListTask task = ToDoListTask.builder()
                .description(description)
                .deadline(deadline)
                .status(TaskStatus.PENDING)
                .todoList(this)
                .build();

        tasks.add(task);
        return task;
    }

    /**
     * Get all deadlines from tasks in this list
     * Aggregates deadlines for calendar view
     *
     * @return List of deadlines from all tasks
     */
    public List<Date> getAggregatedDeadlines() {
        return tasks.stream()
                .map(ToDoListTask::getDeadline)
                .filter(deadline -> deadline != null)
                .toList();
    }
}

package com.cpp.project.timer.repository;

import com.cpp.project.timer.entity.TaskTimer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TaskTimer entity
 * Repository Pattern - Abstracts data access layer
 */
public interface TimerRepository {
    /**
     * Save a timer (create or update)
     *
     * @param timer The timer to save
     * @return The saved timer
     */
    TaskTimer save(TaskTimer timer);

    /**
     * Find timer by ID
     *
     * @param id The timer ID
     * @return Optional containing the timer if found
     */
    Optional<TaskTimer> findById(UUID id);

    /**
     * Find active timer for user and task
     * Returns running timer if exists, empty otherwise
     *
     * @param userId       The user ID
     * @param courseTaskId The course task ID
     * @return Optional containing the running timer if found
     */
    Optional<TaskTimer> findActiveTimerByUserIdAndTaskId(UUID userId, UUID courseTaskId);

    /**
     * Find all timers for a specific task
     *
     * @param courseTaskId The course task ID
     * @return List of timers for the task
     */
    List<TaskTimer> findTimersByTaskId(UUID courseTaskId);

    /**
     * Find all timers for a specific user
     *
     * @param userId The user ID
     * @return List of timers for the user
     */
    List<TaskTimer> findTimersByUserId(UUID userId);

    /**
     * Calculate total time spent on a task (sum of all stopped sessions)
     *
     * @param courseTaskId The course task ID
     * @return Total duration in seconds
     */
    long calculateTotalTimeByTaskId(UUID courseTaskId);

    /**
     * Find all active (running) timers for a user
     *
     * @param userId The user ID
     * @return List of running timers
     */
    List<TaskTimer> findActiveTimersByUserId(UUID userId);

    /**
     * Delete a timer
     *
     * @param timer The timer to delete
     */
    void delete(TaskTimer timer);

    /**
     * Delete timer by ID
     *
     * @param id The timer ID
     */
    void deleteById(UUID id);
}

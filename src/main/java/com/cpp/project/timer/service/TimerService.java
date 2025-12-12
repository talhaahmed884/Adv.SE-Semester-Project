package com.cpp.project.timer.service;

import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.timer.dto.TimerDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Timer operations
 * Facade Pattern - Simplifies complex business logic
 */
public interface TimerService {
    /**
     * Start a new timer for a task
     * Validates no active timer exists for user+task combination
     *
     * @param userId       User ID
     * @param courseTaskId Course task ID
     * @return Created timer DTO
     */
    TimerDTO startTimer(UUID userId, UUID courseTaskId);

    /**
     * Stop an active timer
     * Calculates and stores duration
     *
     * @param timerId Timer ID
     * @param userId  User ID (for authorization check)
     * @return Stopped timer DTO
     */
    TimerDTO stopTimer(UUID timerId, UUID userId);

    /**
     * Get all timer sessions for a task
     *
     * @param courseTaskId Course task ID
     * @return List of timer DTOs
     */
    List<TimerDTO> getTimersByTaskId(UUID courseTaskId);

    /**
     * Get timer summary for a task (total time, sessions, active timer)
     *
     * @param courseTaskId Course task ID
     * @return Task timer summary DTO
     */
    TaskTimerSummaryDTO getTimerSummaryByTaskId(UUID courseTaskId);

    /**
     * Get total accumulated time for a task in seconds
     *
     * @param courseTaskId Course task ID
     * @return Total time in seconds
     */
    long getTotalTimeForTask(UUID courseTaskId);

    /**
     * Stop all active timers for a user (called on logout)
     *
     * @param userId User ID
     */
    void stopAllActiveTimersForUser(UUID userId);

    /**
     * Get a specific timer by ID
     *
     * @param timerId Timer ID
     * @return Timer DTO
     */
    TimerDTO getTimerById(UUID timerId);
}

package com.cpp.project.ui.mediator;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.ui.core.ScreenMediator;

import java.util.List;
import java.util.UUID;

/**
 * Mediator interface for Course screen-state interactions
 * <p>
 * Design Patterns:
 * - Mediator Pattern: Coordinates communication between states
 * - Facade Pattern: Simplifies access to services and data
 */
public interface CourseMediator extends ScreenMediator {

    // ========== Facade: Data Access Methods ==========

    /**
     * Get all courses for current user
     *
     * @return Fresh list of courses from service
     */
    List<CourseDTO> getAllCourses();

    /**
     * Get a specific course by ID
     *
     * @param courseId The course ID
     * @return Fresh course from service
     */
    CourseDTO getCourseById(UUID courseId);

    // ========== Mediator: Action Notification Methods ==========

    /**
     * Called when a new course is created
     * Mediator decides next state (usually return to list view with message)
     */
    void onCourseCreated();

    /**
     * Called when a task is added to a course
     * Mediator decides next state (usually refresh details view)
     *
     * @param courseId The course that was modified
     */
    void onTaskAdded(UUID courseId);

    /**
     * Called when task progress is updated
     * Mediator decides next state (usually refresh details view)
     *
     * @param courseId     The course that was modified
     * @param wasCompleted Whether the task reached 100%
     */
    void onTaskProgressUpdated(UUID courseId, boolean wasCompleted);

    /**
     * Called when user wants to view a specific course
     * Mediator transitions to details state
     *
     * @param courseId The course to view
     */
    void onViewCourseDetails(UUID courseId);

    /**
     * Called when user wants to return to course list
     * Mediator transitions to list view state
     */
    void onReturnToCourseList();

    /**
     * Called when user wants to add a new course
     * Mediator transitions to add course state
     */
    void onAddNewCourse();

    /**
     * Called when user wants to add a task to a course
     * Mediator transitions to add task state
     *
     * @param courseId The course to add task to
     */
    void onAddTaskToCourse(UUID courseId);

    /**
     * Called when user wants to update task progress
     * Mediator transitions to update progress state
     *
     * @param courseId The course containing the task
     * @param taskId   The task to update
     */
    void onUpdateTaskProgress(UUID courseId, UUID taskId);

    /**
     * Called when user wants to edit a course
     * Mediator transitions to edit course state
     *
     * @param courseId The course to edit
     */
    void onEditCourse(UUID courseId);

    /**
     * Called when a course is updated
     * Mediator decides next state (usually refresh details view)
     *
     * @param courseId The course that was updated
     */
    void onCourseUpdated(UUID courseId);

    /**
     * Called when user wants to delete a course
     * Mediator transitions to delete confirmation state
     *
     * @param courseId The course to delete
     */
    void onDeleteCourse(UUID courseId);

    /**
     * Called when a course is deleted
     * Mediator decides next state (usually return to list view with message)
     */
    void onCourseDeleted();

    /**
     * Called when user wants to edit a task
     * Mediator transitions to edit task state
     *
     * @param courseId The course containing the task
     * @param taskId   The task to edit
     */
    void onEditTask(UUID courseId, UUID taskId);

    /**
     * Called when a task is updated
     * Mediator decides next state (usually refresh details view)
     *
     * @param courseId The course containing the task
     */
    void onTaskUpdated(UUID courseId);

    /**
     * Called when user wants to delete a task
     * Mediator transitions to delete confirmation state
     *
     * @param courseId The course containing the task
     * @param taskId   The task to delete
     */
    void onDeleteTask(UUID courseId, UUID taskId);

    /**
     * Called when a task is deleted
     * Mediator decides next state (usually refresh details view)
     *
     * @param courseId The course that was modified
     */
    void onTaskDeleted(UUID courseId);

    // ========== Timer Feature: Data Access Methods ==========

    /**
     * Get timer summary for a specific course task
     * Facade method for states to access timer data
     *
     * @param courseTaskId The task ID to get timer summary for
     * @return Timer summary with total time, sessions, and active timer
     */
    TaskTimerSummaryDTO getTimerSummary(UUID courseTaskId);

    // ========== Timer Feature: Action Notification Methods ==========

    /**
     * Called when user wants to view timer details for a task
     * Mediator transitions to timer view state
     *
     * @param courseId The course containing the task
     * @param taskId   The task to view timer for
     */
    void onViewTimerDetails(UUID courseId, UUID taskId);

    /**
     * Called when a timer is started successfully
     * Mediator decides next state (usually refresh timer view with success message)
     *
     * @param courseId The course containing the task
     * @param taskId   The task that timer was started for
     */
    void onTimerStarted(UUID courseId, UUID taskId);

    /**
     * Called when a timer is stopped successfully
     * Mediator decides next state (usually refresh timer view with success message)
     *
     * @param courseId The course containing the task
     * @param taskId   The task that timer was stopped for
     */
    void onTimerStopped(UUID courseId, UUID taskId);

    /**
     * Called when a timer operation fails
     * Mediator shows error message in current state
     *
     * @param errorMessage The error message to display
     */
    void onTimerError(String errorMessage);

    // ========== Task Details View: Navigation Methods ==========

    /**
     * Called when user wants to view task details
     * Mediator transitions to task details state
     *
     * @param courseId The course containing the task
     * @param taskId   The task to view
     */
    void onViewTaskDetails(UUID courseId, UUID taskId);

    // ========== Task Details View: Context-Aware Entry Points ==========

    /**
     * Called when user wants to update task progress from task details view
     * Mediator transitions to update progress state with return context
     *
     * @param courseId The course containing the task
     * @param taskId   The task to update
     */
    void onUpdateTaskProgressFromTaskDetails(UUID courseId, UUID taskId);

    /**
     * Called when user wants to edit a task from task details view
     * Mediator transitions to edit task state with return context
     *
     * @param courseId The course containing the task
     * @param taskId   The task to edit
     */
    void onEditTaskFromTaskDetails(UUID courseId, UUID taskId);

    /**
     * Called when user wants to delete a task from task details view
     * Mediator transitions to delete task state with return context
     *
     * @param courseId The course containing the task
     * @param taskId   The task to delete
     */
    void onDeleteTaskFromTaskDetails(UUID courseId, UUID taskId);

    // ========== Task Details View: Callback Methods ==========

    /**
     * Called when a task is updated from task details view
     * Mediator returns to task details view with success message
     *
     * @param courseId The course containing the task
     * @param taskId   The task that was updated
     */
    void onTaskUpdatedReturnToTaskDetails(UUID courseId, UUID taskId);

    /**
     * Called when task progress is updated from task details view
     * Mediator returns to task details view with success message
     *
     * @param courseId     The course containing the task
     * @param taskId       The task that was updated
     * @param wasCompleted Whether the task reached 100%
     */
    void onTaskProgressUpdatedReturnToTaskDetails(UUID courseId, UUID taskId, boolean wasCompleted);
}

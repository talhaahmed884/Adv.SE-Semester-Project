package com.cpp.project.ui.mediator;

import com.cpp.project.course.dto.CourseDTO;
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
}

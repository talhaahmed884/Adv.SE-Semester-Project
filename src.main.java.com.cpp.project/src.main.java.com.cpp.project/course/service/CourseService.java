package com.cpp.project.course.service;

import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Course operations
 * Facade Pattern - Simplifies complex business logic
 */
public interface CourseService {
    /**
     * Create a new course
     *
     * @param code   Course code (unique)
     * @param name   Course name
     * @param userId User who owns the course
     * @return Created course DTO
     */
    CourseDTO createCourse(String code, String name, UUID userId);

    /**
     * Get a course by ID
     *
     * @param id Course ID
     * @return Course DTO
     */
    CourseDTO getCourseById(UUID id);

    /**
     * Get a course by code
     *
     * @param code Course code
     * @return Course DTO
     */
    CourseDTO getCourseByCode(String code);

    /**
     * Get all courses for a user
     *
     * @param userId User ID
     * @return List of course DTOs
     */
    List<CourseDTO> getCoursesByUserId(UUID userId);

    /**
     * Update a course
     *
     * @param id   Course ID
     * @param name New course name
     * @return Updated course DTO
     */
    CourseDTO updateCourse(UUID id, String name);

    /**
     * Delete a course
     *
     * @param id Course ID
     */
    void deleteCourse(UUID id);

    /**
     * Add a task to a course
     *
     * @param courseId    Course ID
     * @param name        Task name
     * @param deadline    Task deadline
     * @param description Task description
     * @return Created task DTO
     */
    CourseTaskDTO addTaskToCourse(UUID courseId, String name, Date deadline, String description);

    /**
     * Update task progress
     *
     * @param courseId Course ID
     * @param taskId   Task ID
     * @param progress Progress value (0-100)
     * @return Updated task DTO
     */
    CourseTaskDTO updateTaskProgress(UUID courseId, UUID taskId, int progress);

    /**
     * Mark a task as complete
     *
     * @param courseId Course ID
     * @param taskId   Task ID
     * @return Updated task DTO
     */
    CourseTaskDTO markTaskComplete(UUID courseId, UUID taskId);
}

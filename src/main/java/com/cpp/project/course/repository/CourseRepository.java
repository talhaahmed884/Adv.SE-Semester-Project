package com.cpp.project.course.repository;

import com.cpp.project.course.entity.Course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Course entity
 * Repository Pattern - Abstracts data access layer
 */
public interface CourseRepository {
    /**
     * Save a course (insert or update)
     */
    Course save(Course course);

    /**
     * Find a course by ID
     */
    Optional<Course> findById(UUID id);

    /**
     * Find a course by code
     */
    Optional<Course> findByCode(String code);

    /**
     * Find all courses for a user
     */
    List<Course> findByUserId(UUID userId);

    /**
     * Check if a course with the given code exists
     */
    boolean existsByCode(String code);

    /**
     * Check if a course with the given code exists for a specific user
     */
    boolean existsByCodeAndUserId(String code, UUID userId);

    /**
     * Delete a course entity
     */
    void delete(Course course);

    /**
     * Delete a course by ID
     */
    void deleteById(UUID id);

    /**
     * Find all courses
     */
    List<Course> findAll();

    /**
     * Count total number of courses
     */
    long count();
}

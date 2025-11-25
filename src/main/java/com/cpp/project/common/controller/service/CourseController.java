package com.cpp.project.common.controller.service;

import com.cpp.project.common.controller.dto.ApiSuccessResponse;
import com.cpp.project.course.dto.*;
import com.cpp.project.course.entity.CourseErrorCode;
import com.cpp.project.course.entity.CourseException;
import com.cpp.project.course.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Course operations
 * Follows REST API best practices with ApiSuccessResponse wrapper
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Create a new course
     * POST /api/courses
     */
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<CourseDTO>> createCourse(
            @RequestBody CreateCourseRequestDTO request) {

        // Validate request
        if (request.isEmpty()) {
            throw new CourseException(CourseErrorCode.INVALID_COURSE_DATA,
                    "Course code, name, and userId are required");
        }

        CourseDTO course = courseService.createCourse(
                request.getCode(),
                request.getName(),
                request.getUserId()
        );

        ApiSuccessResponse<CourseDTO> response = ApiSuccessResponse.<CourseDTO>builder()
                .data(course)
                .message("Course created successfully")
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get a course by ID
     * GET /api/courses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<CourseDTO>> getCourseById(
            @PathVariable UUID id) {

        CourseDTO course = courseService.getCourseById(id);

        ApiSuccessResponse<CourseDTO> response = ApiSuccessResponse.<CourseDTO>builder()
                .data(course)
                .message("Course retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get a course by code
     * GET /api/courses/code/{code}
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiSuccessResponse<CourseDTO>> getCourseByCode(
            @PathVariable String code) {

        CourseDTO course = courseService.getCourseByCode(code);

        ApiSuccessResponse<CourseDTO> response = ApiSuccessResponse.<CourseDTO>builder()
                .data(course)
                .message("Course retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get all courses for a user
     * GET /api/courses/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiSuccessResponse<List<CourseDTO>>> getCoursesByUserId(
            @PathVariable UUID userId) {

        List<CourseDTO> courses = courseService.getCoursesByUserId(userId);

        ApiSuccessResponse<List<CourseDTO>> response = ApiSuccessResponse.<List<CourseDTO>>builder()
                .data(courses)
                .message("Courses retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Update a course
     * PUT /api/courses/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<CourseDTO>> updateCourse(
            @PathVariable UUID id,
            @RequestBody UpdateCourseRequestDTO request) {

        // Validate at least one field is provided
        if (request.isEmpty()) {
            throw new CourseException(CourseErrorCode.INVALID_COURSE_DATA,
                    "Course name must be provided");
        }

        CourseDTO course = courseService.updateCourse(id, request.getName());

        ApiSuccessResponse<CourseDTO> response = ApiSuccessResponse.<CourseDTO>builder()
                .data(course)
                .message("Course updated successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a course
     * DELETE /api/courses/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<String>> deleteCourse(
            @PathVariable UUID id) {

        courseService.deleteCourse(id);

        ApiSuccessResponse<String> response = ApiSuccessResponse.<String>builder()
                .data("Course deleted successfully")
                .message("Deletion successful")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Add a task to a course
     * POST /api/courses/{courseId}/tasks
     */
    @PostMapping("/{courseId}/tasks")
    public ResponseEntity<ApiSuccessResponse<CourseTaskDTO>> addTaskToCourse(
            @PathVariable UUID courseId,
            @RequestBody AddTaskRequestDTO request) {

        // Validate request
        if (request.isEmpty()) {
            throw new CourseException(CourseErrorCode.INVALID_TASK_DATA,
                    "Task name and deadline are required");
        }

        CourseTaskDTO task = courseService.addTaskToCourse(
                courseId,
                request.getName(),
                request.getDeadline(),
                request.getDescription()
        );

        ApiSuccessResponse<CourseTaskDTO> response = ApiSuccessResponse.<CourseTaskDTO>builder()
                .data(task)
                .message("Task added successfully")
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update task progress
     * PUT /api/courses/{courseId}/tasks/{taskId}/progress
     */
    @PutMapping("/{courseId}/tasks/{taskId}/progress")
    public ResponseEntity<ApiSuccessResponse<CourseTaskDTO>> updateTaskProgress(
            @PathVariable UUID courseId,
            @PathVariable UUID taskId,
            @RequestBody UpdateTaskProgressRequestDTO request) {

        CourseTaskDTO task = courseService.updateTaskProgress(
                courseId,
                taskId,
                request.getProgress()
        );

        ApiSuccessResponse<CourseTaskDTO> response = ApiSuccessResponse.<CourseTaskDTO>builder()
                .data(task)
                .message("Task progress updated successfully")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Mark a task as complete
     * PUT /api/courses/{courseId}/tasks/{taskId}/complete
     */
    @PutMapping("/{courseId}/tasks/{taskId}/complete")
    public ResponseEntity<ApiSuccessResponse<CourseTaskDTO>> markTaskComplete(
            @PathVariable UUID courseId,
            @PathVariable UUID taskId) {

        CourseTaskDTO task = courseService.markTaskComplete(courseId, taskId);

        ApiSuccessResponse<CourseTaskDTO> response = ApiSuccessResponse.<CourseTaskDTO>builder()
                .data(task)
                .message("Task marked as complete")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}

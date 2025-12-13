package com.cpp.project.course.service;

import com.cpp.project.common.sanitization.adapter.CourseTaskSanitizer;
import com.cpp.project.common.sanitization.adapter.CreateCourseRequestSanitizer;
import com.cpp.project.common.validation.entity.ValidationResult;
import com.cpp.project.common.validation.service.CourseValidationService;
import com.cpp.project.course.adapter.CourseAdapter;
import com.cpp.project.course.adapter.CourseTaskAdapter;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.entity.Course;
import com.cpp.project.course.entity.CourseErrorCode;
import com.cpp.project.course.entity.CourseException;
import com.cpp.project.course.entity.CourseTask;
import com.cpp.project.course.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for Course operations
 * Includes validation, sanitization, and exception handling
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final CourseValidationService validationService;
    private final CreateCourseRequestSanitizer courseSanitizer;
    private final CourseTaskSanitizer taskSanitizer;

    public CourseServiceImpl(CourseRepository courseRepository,
                             CourseValidationService validationService,
                             CreateCourseRequestSanitizer courseSanitizer,
                             CourseTaskSanitizer taskSanitizer) {
        this.courseRepository = courseRepository;
        this.validationService = validationService;
        this.courseSanitizer = courseSanitizer;
        this.taskSanitizer = taskSanitizer;
    }

    // ========== Helper Methods ==========

    /**
     * Find course by ID or throw exception
     *
     * @param courseId Course ID
     * @return Course entity
     * @throws CourseException if course not found
     */
    private Course findCourseOrThrow(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND, "id", courseId));
    }

    /**
     * Find task in course or throw exception
     *
     * @param course Course containing the task
     * @param taskId Task ID
     * @return CourseTask entity
     * @throws CourseException if task not found
     */
    private CourseTask findTaskOrThrow(Course course, UUID taskId) {
        return course.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new CourseException(CourseErrorCode.TASK_NOT_FOUND, taskId));
    }

    /**
     * Sanitize and validate course code
     *
     * @param code Raw course code
     * @return Sanitized and validated course code
     * @throws CourseException if validation fails
     */
    private String sanitizeAndValidateCourseCode(String code) {
        String sanitized = courseSanitizer.sanitizeCode(code);
        ValidationResult validation = validationService.validateCourseCode(sanitized);
        if (!validation.isValid()) {
            throw new CourseException(CourseErrorCode.INVALID_COURSE_CODE, validation.getFirstError());
        }
        return sanitized;
    }

    /**
     * Sanitize and validate course name
     *
     * @param name Raw course name
     * @return Sanitized and validated course name
     * @throws CourseException if validation fails
     */
    private String sanitizeAndValidateCourseName(String name) {
        String sanitized = courseSanitizer.sanitizeName(name);
        ValidationResult validation = validationService.validateCourseName(sanitized);
        if (!validation.isValid()) {
            throw new CourseException(CourseErrorCode.INVALID_COURSE_NAME, validation.getFirstError());
        }
        return sanitized;
    }

    /**
     * Sanitize and validate task name
     *
     * @param name Raw task name
     * @return Sanitized and validated task name
     * @throws CourseException if validation fails
     */
    private String sanitizeAndValidateTaskName(String name) {
        String sanitized = taskSanitizer.sanitizeName(name);
        ValidationResult validation = validationService.validateTaskName(sanitized);
        if (!validation.isValid()) {
            throw new CourseException(CourseErrorCode.INVALID_TASK_NAME, validation.getFirstError());
        }
        return sanitized;
    }

    /**
     * Sanitize task description
     *
     * @param description Raw task description
     * @return Sanitized task description
     */
    private String sanitizeTaskDescription(String description) {
        return taskSanitizer.sanitizeDescription(description);
    }

    /**
     * Validate task deadline
     *
     * @param deadline Task deadline
     * @throws CourseException if validation fails
     */
    private void validateTaskDeadline(Instant deadline) {
        ValidationResult validation = validationService.validateTaskDeadline(deadline);
        if (!validation.isValid()) {
            throw new CourseException(CourseErrorCode.INVALID_TASK_DEADLINE, validation.getFirstError());
        }
    }

    /**
     * Validate task progress
     *
     * @param progress Task progress (0-100)
     * @throws CourseException if validation fails
     */
    private void validateTaskProgress(int progress) {
        ValidationResult validation = validationService.validateTaskProgress(progress);
        if (!validation.isValid()) {
            throw new CourseException(CourseErrorCode.INVALID_TASK_PROGRESS, progress);
        }
    }

    // ========== Public Service Methods ==========

    @Override
    public CourseDTO createCourse(String code, String name, UUID userId) {
        logger.info("Creating course with code: {}", code);

        try {
            // Sanitize and validate input
            String sanitizedCode = sanitizeAndValidateCourseCode(code);
            String sanitizedName = sanitizeAndValidateCourseName(name);

            // Check if course with code already exists for this user
            if (courseRepository.existsByCodeAndUserId(sanitizedCode, userId)) {
                throw new CourseException(CourseErrorCode.COURSE_ALREADY_EXISTS, sanitizedCode);
            }

            // Create course
            Course course = Course.builder()
                    .code(sanitizedCode)
                    .name(sanitizedName)
                    .userId(userId)
                    .build();

            Course savedCourse = courseRepository.save(course);
            logger.info("Course created successfully: {}", savedCourse.getCode());

            return CourseAdapter.toDTO(savedCourse);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating course", e);
            throw new CourseException(CourseErrorCode.COURSE_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(UUID id) {
        logger.debug("Getting course by id: {}", id);

        try {
            Course course = findCourseOrThrow(id);
            return CourseAdapter.toDTO(course);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error getting course by id", e);
            throw new CourseException(CourseErrorCode.COURSE_NOT_FOUND, e, "id", id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseByCode(String code) {
        logger.debug("Getting course by code: {}", code);

        // Sanitize code before lookup
        String sanitizedCode = courseSanitizer.sanitizeCode(code);

        Course course = courseRepository.findByCode(sanitizedCode)
                .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND, "code", sanitizedCode));

        return CourseAdapter.toDTO(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByUserId(UUID userId) {
        logger.debug("Getting courses for user: {}", userId);

        List<Course> courses = courseRepository.findByUserId(userId);

        return courses.stream()
                .map(CourseAdapter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDTO updateCourse(UUID id, String name) {
        logger.info("Updating course: {}", id);

        try {
            // Sanitize and validate input
            String sanitizedName = sanitizeAndValidateCourseName(name);

            // Update course
            Course course = findCourseOrThrow(id);
            course.setName(sanitizedName);
            Course updatedCourse = courseRepository.save(course);

            logger.info("Course updated successfully: {}", id);
            return CourseAdapter.toDTO(updatedCourse);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error updating course: {}", id, e);
            throw new CourseException(CourseErrorCode.COURSE_UPDATE_FAILED, e, e.getMessage());
        }
    }

    @Override
    public void deleteCourse(UUID id) {
        logger.info("Deleting course: {}", id);

        try {
            if (courseRepository.findById(id).isEmpty()) {
                throw new CourseException(CourseErrorCode.COURSE_NOT_FOUND, "id", id);
            }

            courseRepository.deleteById(id);
            logger.info("Course deleted successfully: {}", id);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error deleting course: {}", id, e);
            throw new CourseException(CourseErrorCode.COURSE_DELETION_FAILED, e, id.toString());
        }
    }

    @Override
    public CourseTaskDTO addTaskToCourse(UUID courseId, String name, Instant deadline, String description) {
        logger.info("Adding task to course: {}", courseId);

        try {
            // Sanitize and validate input
            String sanitizedName = sanitizeAndValidateTaskName(name);
            String sanitizedDescription = sanitizeTaskDescription(description);
            validateTaskDeadline(deadline);

            // Find course and add task
            Course course = findCourseOrThrow(courseId);
            CourseTask task = course.addTask(sanitizedName, deadline, sanitizedDescription);

            courseRepository.save(course);
            logger.info("Task added to course successfully");

            return CourseTaskAdapter.toDTO(task);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error adding task to course: {}", courseId, e);
            throw new CourseException(CourseErrorCode.TASK_CREATION_FAILED, e, e.getMessage());
        }
    }

    @Override
    public CourseTaskDTO updateTaskProgress(UUID courseId, UUID taskId, int progress) {
        logger.info("Updating task progress: {} in course: {}", taskId, courseId);

        try {
            // Validate progress
            validateTaskProgress(progress);

            // Find course and task
            Course course = findCourseOrThrow(courseId);
            CourseTask task = findTaskOrThrow(course, taskId);

            // Update progress
            task.updateProgress(progress);

            courseRepository.save(course);
            logger.info("Task progress updated successfully");

            return CourseTaskAdapter.toDTO(task);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error updating task progress: {}", taskId, e);
            throw new CourseException(CourseErrorCode.TASK_UPDATE_FAILED, e, e.getMessage());
        }
    }

    @Override
    public CourseTaskDTO markTaskComplete(UUID courseId, UUID taskId) {
        logger.info("Marking task complete: {} in course: {}", taskId, courseId);

        try {
            Course course = findCourseOrThrow(courseId);
            CourseTask task = findTaskOrThrow(course, taskId);

            task.markComplete();

            courseRepository.save(course);
            logger.info("Task marked complete successfully");

            return CourseTaskAdapter.toDTO(task);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error marking task complete: {}", taskId, e);
            throw new CourseException(CourseErrorCode.TASK_UPDATE_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional
    public CourseTaskDTO updateTask(UUID courseId, UUID taskId, String name, Instant deadline, String description) {
        logger.info("Updating task: {} in course: {}", taskId, courseId);

        try {
            // Sanitize and validate input
            String sanitizedName = sanitizeAndValidateTaskName(name);
            String sanitizedDescription = sanitizeTaskDescription(description);
            validateTaskDeadline(deadline);

            // Find course and task
            Course course = findCourseOrThrow(courseId);
            CourseTask task = findTaskOrThrow(course, taskId);

            // Update task fields
            task.setName(sanitizedName);
            task.setDescription(sanitizedDescription);
            task.setDeadline(deadline);

            // Save changes
            courseRepository.save(course);
            logger.info("Task updated successfully");

            return CourseTaskAdapter.toDTO(task);
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error updating task: {}", taskId, e);
            throw new CourseException(CourseErrorCode.TASK_UPDATE_FAILED, e, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteTask(UUID courseId, UUID taskId) {
        logger.info("Deleting task: {} from course: {}", taskId, courseId);

        try {
            // Find course
            Course course = findCourseOrThrow(courseId);

            // Find and remove task
            CourseTask task = findTaskOrThrow(course, taskId);
            course.getTasks().remove(task);

            // Save changes
            courseRepository.save(course);
            logger.info("Task deleted successfully");
        } catch (CourseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error deleting task: {}", taskId, e);
            throw new CourseException(CourseErrorCode.TASK_DELETE_FAILED, e, e.getMessage());
        }
    }
}

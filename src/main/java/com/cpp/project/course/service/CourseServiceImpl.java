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

import java.util.Date;
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

    @Override
    public CourseDTO createCourse(String code, String name, UUID userId) {
        logger.info("Creating course with code: {}", code);

        try {
            // Step 1: Sanitize input
            String sanitizedCode = courseSanitizer.sanitizeCode(code);
            String sanitizedName = courseSanitizer.sanitizeName(name);

            // Step 2: Validate sanitized input
            ValidationResult codeValidation = validationService.validateCourseCode(sanitizedCode);
            if (!codeValidation.isValid()) {
                throw new CourseException(CourseErrorCode.INVALID_COURSE_CODE, codeValidation.getFirstError());
            }

            ValidationResult nameValidation = validationService.validateCourseName(sanitizedName);
            if (!nameValidation.isValid()) {
                throw new CourseException(CourseErrorCode.INVALID_COURSE_NAME, nameValidation.getFirstError());
            }

            // Step 3: Check if course with code already exists for this user
            if (courseRepository.existsByCodeAndUserId(sanitizedCode, userId)) {
                throw new CourseException(CourseErrorCode.COURSE_ALREADY_EXISTS, sanitizedCode);
            }

            // Step 4: Create course
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

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND, "id", id));

        return CourseAdapter.toDTO(course);
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
                .map(CourseAdapter::toDTOWithoutTasks)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDTO updateCourse(UUID id, String name) {
        logger.info("Updating course: {}", id);

        try {
            // Step 1: Sanitize input
            String sanitizedName = courseSanitizer.sanitizeName(name);

            // Step 2: Validate sanitized input
            ValidationResult nameValidation = validationService.validateCourseName(sanitizedName);
            if (!nameValidation.isValid()) {
                throw new CourseException(CourseErrorCode.INVALID_COURSE_NAME, nameValidation.getFirstError());
            }

            // Step 3: Update course
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND, "id", id));

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
    public CourseTaskDTO addTaskToCourse(UUID courseId, String name, Date deadline, String description) {
        logger.info("Adding task to course: {}", courseId);

        try {
            // Step 1: Sanitize input
            String sanitizedName = taskSanitizer.sanitizeName(name);
            String sanitizedDescription = taskSanitizer.sanitizeDescription(description);

            // Step 2: Validate sanitized input
            ValidationResult nameValidation = validationService.validateTaskName(sanitizedName);
            if (!nameValidation.isValid()) {
                throw new CourseException(CourseErrorCode.INVALID_TASK_NAME, nameValidation.getFirstError());
            }

            // Step 3: Find course and add task
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND, "id", courseId));

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
            // Step 1: Validate progress
            ValidationResult progressValidation = validationService.validateTaskProgress(progress);
            if (!progressValidation.isValid()) {
                throw new CourseException(CourseErrorCode.INVALID_TASK_PROGRESS, progress);
            }

            // Step 2: Find course and task
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND, "id", courseId));

            CourseTask task = course.getTasks().stream()
                    .filter(t -> t.getId().equals(taskId))
                    .findFirst()
                    .orElseThrow(() -> new CourseException(CourseErrorCode.TASK_NOT_FOUND, taskId));

            // Step 3: Update progress
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
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND, "id", courseId));

            CourseTask task = course.getTasks().stream()
                    .filter(t -> t.getId().equals(taskId))
                    .findFirst()
                    .orElseThrow(() -> new CourseException(CourseErrorCode.TASK_NOT_FOUND, taskId));

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
}

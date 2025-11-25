package com.cpp.project.uc_4_add_course_task;

import com.cpp.project.common.sanitization.adapter.CourseTaskSanitizer;
import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.service.CourseValidationService;
import com.cpp.project.course.entity.Course;
import com.cpp.project.course.entity.CourseErrorCode;
import com.cpp.project.course.entity.CourseException;
import com.cpp.project.course.repository.CourseRepository;
import com.cpp.project.course.service.CourseServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UC-4.12: Attempts to add course's task when repository ran into failure
 * This is a unit test using Mockito to simulate repository failure
 */
@ExtendWith(MockitoExtension.class)
public class UC_4_12_AddCourseTask_WhenRepositoryFails_Test {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseValidationService validationService;

    @Mock
    private CourseTaskSanitizer taskSanitizer;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    @DisplayName("UC-4.12: Throws exception when repository fails to save task")
    public void testAddCourseTaskWhenRepositoryFails() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        String taskName = "Assignment 1";
        Date deadline = new Date(System.currentTimeMillis() + 86400000); // tomorrow
        String description = "Complete the assignment";

        // Mock sanitization
        when(taskSanitizer.sanitizeName(taskName)).thenReturn(taskName);
        when(taskSanitizer.sanitizeDescription(description)).thenReturn(description);

        // Mock validation - return valid results
        when(validationService.validateTaskName(any())).thenReturn(
                new ValidationResultBuilder().build()
        );

        when(validationService.validateTaskDeadline(any())).thenReturn(new ValidationResultBuilder().build());

        // Mock course retrieval - return a mock course
        Course mockCourse = Course.builder()
                .code("CS101")
                .name("Introduction to CS")
                .userId(UUID.randomUUID())
                .build();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));

        // Mock repository save to throw exception
        when(courseRepository.save(any(Course.class)))
                .thenThrow(new jakarta.persistence.PersistenceException("Database connection failed"));

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.addTaskToCourse(courseId, taskName, deadline, description);
        });

        assertEquals(CourseErrorCode.TASK_CREATION_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));

        // Verify that repository save was attempted
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("UC-4.12: Throws exception when course not found")
    public void testAddCourseTaskWhenCourseNotFound() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        String taskName = "Assignment 1";
        Date deadline = new Date(System.currentTimeMillis() + 86400000); // tomorrow
        String description = "Complete the assignment";

        // Mock sanitization
        when(taskSanitizer.sanitizeName(taskName)).thenReturn(taskName);
        when(taskSanitizer.sanitizeDescription(description)).thenReturn(description);

        // Mock validation
        when(validationService.validateTaskName(any())).thenReturn(
                new ValidationResultBuilder().build()
        );

        when(validationService.validateTaskDeadline(any())).thenReturn(new ValidationResultBuilder().build());

        // Mock repository to return empty (course not found)
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.addTaskToCourse(courseId, taskName, deadline, description);
        });

        assertEquals(CourseErrorCode.COURSE_NOT_FOUND.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("not found"));

        // Verify that repository findById was called
        verify(courseRepository, times(1)).findById(courseId);
    }
}

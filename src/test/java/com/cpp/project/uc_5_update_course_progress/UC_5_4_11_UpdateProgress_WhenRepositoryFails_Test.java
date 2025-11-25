package com.cpp.project.uc_5_update_course_progress;

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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UC-5.4.11: Attempts to update course's task progress when repository ran into failure
 * This is a unit test using Mockito to simulate repository failure
 */
@ExtendWith(MockitoExtension.class)
public class UC_5_4_11_UpdateProgress_WhenRepositoryFails_Test {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseValidationService validationService;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    @DisplayName("UC-5.4.11: Throws exception when repository fails to save progress update")
    public void testUpdateProgressWhenRepositoryFails() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        int progress = 50;

        // Mock validation - return valid result
        when(validationService.validateTaskProgress(progress)).thenReturn(
                new ValidationResultBuilder().build()
        );

        // Mock course retrieval - return a mock course with a task
        Course mockCourse = Course.builder()
                .code("CS101")
                .name("Introduction to CS")
                .userId(UUID.randomUUID())
                .build();

        // Add a mock task to the course
        mockCourse.addTask("Task 1", new java.util.Date(System.currentTimeMillis() + 86400000), "Description");
        mockCourse.getTasks().getFirst().setId(taskId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));

        // Mock repository save to throw exception
        when(courseRepository.save(any(Course.class)))
                .thenThrow(new jakarta.persistence.PersistenceException("Database connection failed"));

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.updateTaskProgress(courseId, taskId, progress);
        });

        assertEquals(CourseErrorCode.TASK_UPDATE_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));

        // Verify that repository save was attempted
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("UC-5.4.11: Throws exception when course not found")
    public void testUpdateProgressWhenCourseNotFound() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        int progress = 50;

        // Mock validation
        when(validationService.validateTaskProgress(progress)).thenReturn(
                new ValidationResultBuilder().build()
        );

        // Mock repository to return empty (course not found)
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.updateTaskProgress(courseId, taskId, progress);
        });

        assertEquals(CourseErrorCode.COURSE_NOT_FOUND.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("not found"));

        // Verify that repository findById was called
        verify(courseRepository, times(1)).findById(courseId);
    }
}

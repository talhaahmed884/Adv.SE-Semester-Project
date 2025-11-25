package com.cpp.project.uc_6_mark_course_task_complete;

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
 * UC-6.4.18: Attempts to mark course's task complete when repository ran into failure
 * This is a unit test using Mockito to simulate repository failure
 */
@ExtendWith(MockitoExtension.class)
public class UC_6_4_18_MarkComplete_WhenRepositoryFails_Test {
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    @DisplayName("UC-6.4.18: Throws exception when repository fails to save mark complete")
    public void testMarkCompleteWhenRepositoryFails() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        // Mock course retrieval - return a mock course with a task
        Course mockCourse = Course.builder()
                .code("CS101")
                .name("Introduction to CS")
                .userId(UUID.randomUUID())
                .build();

        // Add a mock task to the course
        mockCourse.addTask("Task 1", new Date(System.currentTimeMillis() + 86400000), "Description");
        mockCourse.getTasks().getFirst().setId(taskId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));

        // Mock repository save to throw exception
        when(courseRepository.save(any(Course.class)))
                .thenThrow(new jakarta.persistence.PersistenceException("Database connection failed"));

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.markTaskComplete(courseId, taskId);
        });

        assertEquals(CourseErrorCode.TASK_UPDATE_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));

        // Verify that repository save was attempted
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("UC-6.4.18: Throws exception when course not found")
    public void testMarkCompleteWhenCourseNotFound() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        // Mock repository to return empty (course not found)
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.markTaskComplete(courseId, taskId);
        });

        assertEquals(CourseErrorCode.COURSE_NOT_FOUND.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("not found"));

        // Verify that repository findById was called
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    @DisplayName("UC-6.4.18: Throws exception when task not found in course")
    public void testMarkCompleteWhenTaskNotFound() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        UUID nonExistentTaskId = UUID.randomUUID();

        // Mock course retrieval - return a mock course with a different task
        Course mockCourse = Course.builder()
                .code("CS101")
                .name("Introduction to CS")
                .userId(UUID.randomUUID())
                .build();

        // Add a task with a different ID
        mockCourse.addTask("Task 1", new Date(System.currentTimeMillis() + 86400000), "Description");
        mockCourse.getTasks().getFirst().setId(UUID.randomUUID());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.markTaskComplete(courseId, nonExistentTaskId);
        });

        assertEquals(CourseErrorCode.TASK_NOT_FOUND.getCode(), exception.getCode());

        // Verify that repository findById was called but save was not
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }
}

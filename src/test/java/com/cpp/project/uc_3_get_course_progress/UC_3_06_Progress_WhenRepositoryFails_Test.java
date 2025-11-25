package com.cpp.project.uc_3_get_course_progress;

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
 * UC-3.06: Attempts to query course's tasks when repository ran into failure
 * This is a unit test using Mockito to simulate repository failure
 */
@ExtendWith(MockitoExtension.class)
public class UC_3_06_Progress_WhenRepositoryFails_Test {
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    @DisplayName("UC-3.06: Throws exception when repository fails to retrieve course")
    public void testProgressWhenRepositoryFails() {
        // Arrange
        UUID courseId = UUID.randomUUID();

        // Mock repository to throw exception when trying to retrieve course
        when(courseRepository.findById(any(UUID.class)))
                .thenThrow(new jakarta.persistence.PersistenceException("Database connection failed"));

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.getCourseById(courseId);
        });

        assertEquals(CourseErrorCode.COURSE_NOT_FOUND.getCode(), exception.getCode());

        // Verify that repository findById was attempted
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    @DisplayName("UC-3.06: Returns empty optional when course not found")
    public void testProgressWhenCourseNotFound() {
        // Arrange
        UUID courseId = UUID.randomUUID();

        // Mock repository to return empty optional
        when(courseRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.getCourseById(courseId);
        });

        assertEquals(CourseErrorCode.COURSE_NOT_FOUND.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("not found"));

        // Verify that repository findById was attempted
        verify(courseRepository, times(1)).findById(courseId);
    }
}

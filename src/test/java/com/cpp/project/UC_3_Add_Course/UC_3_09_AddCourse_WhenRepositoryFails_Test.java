package com.cpp.project.UC_3_Add_Course;

import com.cpp.project.common.sanitization.adapter.CreateCourseRequestSanitizer;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * UC-3.09: Attempts to add course when repository ran into runtime failure
 * This is a unit test using Mockito to simulate repository failure
 */
@ExtendWith(MockitoExtension.class)
public class UC_3_09_AddCourse_WhenRepositoryFails_Test {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseValidationService validationService;

    @Mock
    private CreateCourseRequestSanitizer courseSanitizer;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    @DisplayName("UC-3.09: Throws exception when repository fails during save")
    public void testAddCourseWhenRepositoryFails() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String code = "CS101";
        String name = "Introduction to CS";

        // Mock sanitization
        when(courseSanitizer.sanitizeCode(code)).thenReturn("CS101");
        when(courseSanitizer.sanitizeName(name)).thenReturn(name);

        // Mock validation - return valid results
        when(validationService.validateCourseCode(any())).thenReturn(
                new ValidationResultBuilder().build()
        );
        when(validationService.validateCourseName(any())).thenReturn(
                new ValidationResultBuilder().build()
        );

        // Mock repository - existsByCodeAndUserId returns false, but save throws exception
        when(courseRepository.existsByCodeAndUserId("CS101", userId)).thenReturn(false);
        when(courseRepository.save(any(Course.class)))
                .thenThrow(new jakarta.persistence.PersistenceException("Database connection failed"));

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse(code, name, userId);
        });

        assertEquals(CourseErrorCode.COURSE_CREATION_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));

        // Verify that repository save was attempted
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("UC-3.09: Throws exception when repository existsByCodeAndUserId check fails")
    public void testAddCourseWhenRepositoryExistsByCodeAndUserIdFails() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String code = "CS101";
        String name = "Introduction to CS";

        // Mock sanitization
        when(courseSanitizer.sanitizeCode(code)).thenReturn("CS101");
        when(courseSanitizer.sanitizeName(name)).thenReturn(name);

        // Mock validation
        when(validationService.validateCourseCode(any())).thenReturn(
                new ValidationResultBuilder().build()
        );
        when(validationService.validateCourseName(any())).thenReturn(
                new ValidationResultBuilder().build()
        );

        // Mock repository - existsByCodeAndUserId throws exception
        when(courseRepository.existsByCodeAndUserId(eq("CS101"), any(UUID.class)))
                .thenThrow(new RuntimeException("Database connection timeout"));

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse(code, name, userId);
        });

        assertEquals(CourseErrorCode.COURSE_CREATION_FAILED.getCode(), exception.getCode());
    }
}

package com.cpp.project.uc_3_add_course;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.entity.CourseErrorCode;
import com.cpp.project.course.entity.CourseException;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-3.08: Rejects adding a course with a code that violates the code pattern
 * Pattern: ^[A-Z0-9_-]+$
 */
public class UC_3_26_AddCourse_WithInvalidCodePattern_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.08: Accepts valid code pattern (uppercase alphanumeric with dash/underscore)")
    public void testAddCourseValidCodePattern() {
        // Arrange - Create test users
        UserDTO user1 = authenticationService.signUp(new SignUpRequestDTO(
                "User One", "test.user1.uc308a@test.com", "Password123!"));
        UserDTO user2 = authenticationService.signUp(new SignUpRequestDTO(
                "User Two", "test.user2.uc308a@test.com", "Password123!"));
        UserDTO user3 = authenticationService.signUp(new SignUpRequestDTO(
                "User Three", "test.user3.uc308a@test.com", "Password123!"));

        // Act - Various valid patterns
        CourseDTO result1 = courseService.createCourse("CS101", "Course 1", user1.getId());
        CourseDTO result2 = courseService.createCourse("MATH-200", "Course 2", user2.getId());
        CourseDTO result3 = courseService.createCourse("ENG_101", "Course 3", user3.getId());

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals("CS101", result1.getCode());
        assertEquals("MATH-200", result2.getCode());
        assertEquals("ENG_101", result3.getCode());
    }

    @Test
    @DisplayName("UC-3.08: Rejects code with special characters (@#$)")
    public void testAddCourseInvalidCodeWithSpecialChars() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User", "test.user.uc308b@test.com", "Password123!"));
        UUID userId = user.getId();

        // Act & Assert - Special characters
        CourseException exception1 = assertThrows(CourseException.class, () -> {
            courseService.createCourse("CS@101", "Course Name", userId);
        });
        assertEquals(CourseErrorCode.INVALID_COURSE_CODE.getCode(), exception1.getCode());

        CourseException exception2 = assertThrows(CourseException.class, () -> {
            courseService.createCourse("CS#101", "Course Name", userId);
        });
        assertEquals(CourseErrorCode.INVALID_COURSE_CODE.getCode(), exception2.getCode());

        CourseException exception3 = assertThrows(CourseException.class, () -> {
            courseService.createCourse("CS$101", "Course Name", userId);
        });
        assertEquals(CourseErrorCode.INVALID_COURSE_CODE.getCode(), exception3.getCode());
    }

    @Test
    @DisplayName("UC-3.08: Rejects code with spaces")
    public void testAddCourseInvalidCodeWithSpaces() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User", "test.user.uc308c@test.com", "Password123!"));
        UUID userId = user.getId();

        // Act & Assert - Note: Leading/trailing spaces are trimmed,
        // but middle spaces should be rejected
        CourseException exception = assertThrows(CourseException.class, () -> {
            // This should fail because after trimming, "CS 101" has space in middle
            courseService.createCourse("CS 101", "Course Name", userId);
        });

        assertEquals(CourseErrorCode.INVALID_COURSE_CODE.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("uppercase") ||
                exception.getMessage().toLowerCase().contains("alphanumeric") ||
                exception.getMessage().toLowerCase().contains("pattern"));
    }

    @Test
    @DisplayName("UC-3.08: Rejects lowercase code (after normalization check)")
    public void testAddCourseRejectsLowercaseBeforeNormalization() {
        // Note: Since we have code normalization (uppercase),
        // lowercase codes are automatically converted to uppercase
        // This test verifies that the pattern validation happens AFTER normalization

        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User", "test.user.uc308d@test.com", "Password123!"));
        UUID userId = user.getId();

        // Act - lowercase code should be normalized to uppercase and accepted
        CourseDTO result = courseService.createCourse("cs101", "Course Name", userId);

        // Assert - Should be uppercased and accepted
        assertNotNull(result);
        assertEquals("CS101", result.getCode());
    }
}

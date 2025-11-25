package com.cpp.project.uc_3_add_course;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.entity.CourseErrorCode;
import com.cpp.project.course.entity.CourseException;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-3.07: Accepts min/max lengths for code and name; rejects beyond max
 * Based on CourseCodeValidator: MIN=2, MAX=20
 * Based on CourseNameValidator: MIN=3, MAX=255
 */
public class UC_3_07_AddCourse_Boundaries_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    private UUID userId;

    @BeforeEach
    public void setup() {
        // Create a test user to use across all test methods
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc307@test.com",
                "Password123!"
        ));
        userId = user.getId();
    }

    @Test
    @DisplayName("UC-3.07: Accepts minimum valid code length (2 characters)")
    public void testAddCourseMinCodeLength() {
        // Arrange
        String code = "AB"; // 2 characters - minimum valid

        // Act
        CourseDTO result = courseService.createCourse(code, "Course Name", userId);

        // Assert
        assertNotNull(result);
        assertEquals("AB", result.getCode());
    }

    @Test
    @DisplayName("UC-3.07: Accepts maximum valid code length (20 characters)")
    public void testAddCourseMaxCodeLength() {
        // Arrange
        String code = "ABCDEFGHIJ1234567890"; // 20 characters - maximum valid

        // Act
        CourseDTO result = courseService.createCourse(code, "Course Name", userId);

        // Assert
        assertNotNull(result);
        assertEquals(code, result.getCode());
    }

    @Test
    @DisplayName("UC-3.07: Rejects code length beyond maximum (21 characters)")
    public void testAddCourseCodeTooLong() {
        // Arrange
        String code = "ABCDEFGHIJ12345678901"; // 21 characters - exceeds maximum

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse(code, "Course Name", userId);
        });

        assertEquals(CourseErrorCode.INVALID_COURSE_CODE.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("exceed") ||
                exception.getMessage().toLowerCase().contains("20"));
    }

    @Test
    @DisplayName("UC-3.07: Accepts minimum valid name length (3 characters)")
    public void testAddCourseMinNameLength() {
        // Arrange
        String name = "ABC"; // 3 characters - minimum valid

        // Act
        CourseDTO result = courseService.createCourse("CS101", name, userId);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
    }

    @Test
    @DisplayName("UC-3.07: Accepts maximum valid name length (255 characters)")
    public void testAddCourseMaxNameLength() {
        // Arrange
        String name = "A".repeat(255); // 255 characters - maximum valid

        // Act
        CourseDTO result = courseService.createCourse("CS102", name, userId);

        // Assert
        assertNotNull(result);
        assertEquals(255, result.getName().length());
    }

    @Test
    @DisplayName("UC-3.07: Rejects name length beyond maximum (256 characters)")
    public void testAddCourseNameTooLong() {
        // Arrange
        String name = "A".repeat(256); // 256 characters - exceeds maximum

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse("CS103", name, userId);
        });

        assertEquals(CourseErrorCode.INVALID_COURSE_NAME.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("exceed") ||
                exception.getMessage().toLowerCase().contains("255"));
    }
}

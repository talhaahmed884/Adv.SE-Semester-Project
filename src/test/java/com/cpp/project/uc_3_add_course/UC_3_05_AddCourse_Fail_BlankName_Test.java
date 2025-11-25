package com.cpp.project.uc_3_add_course;

import com.cpp.project.authentication.service.AuthenticationService;
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
 * UC-3.05: Course name is null or blank
 */
public class UC_3_05_AddCourse_Fail_BlankName_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.05: Rejects null course name")
    public void testAddCourseNullName() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc305a@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse("CS101", null, userId);
        });

        assertEquals(CourseErrorCode.INVALID_COURSE_NAME.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("name"));
    }

    @Test
    @DisplayName("UC-3.05: Rejects empty course name")
    public void testAddCourseEmptyName() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc305b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse("CS101", "", userId);
        });

        assertEquals(CourseErrorCode.INVALID_COURSE_NAME.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("UC-3.05: Rejects whitespace-only course name")
    public void testAddCourseWhitespaceName() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc305c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse("CS101", "   ", userId);
        });

        assertEquals(CourseErrorCode.INVALID_COURSE_NAME.getCode(), exception.getCode());
    }
}

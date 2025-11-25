package com.cpp.project.UC_3_Add_Course;

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
 * UC-3.04: Code is null, empty, or whitespace
 */
public class UC_3_04_AddCourse_Fail_BlankCode_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.04: Rejects null course code")
    public void testAddCourseNullCode() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc304a@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse(null, "Course Name", userId);
        });

        assertEquals(CourseErrorCode.INVALID_COURSE_CODE.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("code"));
    }

    @Test
    @DisplayName("UC-3.04: Rejects empty course code")
    public void testAddCourseEmptyCode() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc304b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse("", "Course Name", userId);
        });

        assertEquals(CourseErrorCode.INVALID_COURSE_CODE.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("UC-3.04: Rejects whitespace-only course code")
    public void testAddCourseWhitespaceCode() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc304c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse("   ", "Course Name", userId);
        });

        assertEquals(CourseErrorCode.INVALID_COURSE_CODE.getCode(), exception.getCode());
    }
}

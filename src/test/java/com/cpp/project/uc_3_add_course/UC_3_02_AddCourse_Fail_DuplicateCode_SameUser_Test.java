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
 * UC-3.02: Rejects adding a course with a code that already exists for this user
 */
public class UC_3_02_AddCourse_Fail_DuplicateCode_SameUser_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.02: Rejects adding a course with a code that already exists for this user")
    public void testAddCourseDuplicateCodeSameUser() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc302a@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        String code = "CS101";

        // Create first course
        courseService.createCourse(code, "First Course", userId);

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse(code, "Second Course", userId);
        });

        assertEquals(CourseErrorCode.COURSE_ALREADY_EXISTS.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("CS101"));
    }

    @Test
    @DisplayName("UC-3.02: Rejects duplicate code ignoring case and whitespace")
    public void testAddCourseDuplicateCodeIgnoringCaseAndWhitespace() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc302b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create first course with "CS101"
        courseService.createCourse("CS101", "First Course", userId);

        // Act & Assert - Try to create with " cs101 " (different case and whitespace)
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.createCourse(" cs101 ", "Second Course", userId);
        });

        assertEquals(CourseErrorCode.COURSE_ALREADY_EXISTS.getCode(), exception.getCode());
    }
}

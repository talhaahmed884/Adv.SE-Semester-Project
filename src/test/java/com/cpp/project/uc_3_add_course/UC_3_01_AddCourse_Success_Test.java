package com.cpp.project.uc_3_add_course;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-3.01: Adds a new course with a unique code for the user
 */
public class UC_3_01_AddCourse_Success_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.01: Adds a new course with a unique code for the user")
    public void testAddCourseSuccess() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc301@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        String code = "CS101";
        String name = "Introduction to Computer Science";

        // Act
        CourseDTO result = courseService.createCourse(code, name, userId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("CS101", result.getCode()); // Should be uppercased
        assertEquals("Introduction to Computer Science", result.getName());
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getProgress());
    }
}

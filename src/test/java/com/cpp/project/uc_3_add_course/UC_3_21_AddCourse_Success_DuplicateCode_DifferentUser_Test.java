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

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-3.03: Allows same course code for a different user (per-user uniqueness)
 */
public class UC_3_21_AddCourse_Success_DuplicateCode_DifferentUser_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.03: Allows same course code for a different user")
    public void testAddCourseDuplicateCodeDifferentUser() {
        // Arrange - Create two test users
        UserDTO user1 = authenticationService.signUp(new SignUpRequestDTO(
                "User One",
                "test.user1.uc303@test.com",
                "Password123!"
        ));
        UUID user1Id = user1.getId();

        UserDTO user2 = authenticationService.signUp(new SignUpRequestDTO(
                "User Two",
                "test.user2.uc303@test.com",
                "Password123!"
        ));
        UUID user2Id = user2.getId();

        String code = "CS101";

        // Act
        // Create course for user1
        CourseDTO course1 = courseService.createCourse(code, "Intro to CS - User 1", user1Id);

        // Create course with same code for user2 - should succeed
        CourseDTO course2 = courseService.createCourse(code, "Intro to CS - User 2", user2Id);

        // Assert
        assertNotNull(course1);
        assertNotNull(course2);
        assertEquals("CS101", course1.getCode());
        assertEquals("CS101", course2.getCode());
        assertEquals(user1Id, course1.getUserId());
        assertEquals(user2Id, course2.getUserId());
        assertNotEquals(course1.getId(), course2.getId());
    }
}

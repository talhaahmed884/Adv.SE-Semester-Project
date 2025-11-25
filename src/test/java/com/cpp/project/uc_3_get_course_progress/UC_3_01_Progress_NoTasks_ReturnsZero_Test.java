package com.cpp.project.uc_3_get_course_progress;

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
 * UC-3.01: Progress is 0 when there are no tasks
 */
public class UC_3_01_Progress_NoTasks_ReturnsZero_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.01: Progress is 0 when there are no tasks")
    public void testProgressNoTasksReturnsZero() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc301@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course with no tasks
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Act
        CourseDTO result = courseService.getCourseById(course.getId());

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getProgress());
    }
}

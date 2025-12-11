package com.cpp.project.uc_11_update_course_task;

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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-11.02: Fails to update task when task is not found
 */
public class UC_11_02_UpdateCourseTask_Fail_TaskNotFound_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-11.02: Fails to update task with non-existent task ID")
    public void testUpdateNonExistentTask() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetask.uc1102@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Use a random UUID that doesn't exist
        UUID nonExistentTaskId = UUID.randomUUID();
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.updateTask(
                    course.getId(),
                    nonExistentTaskId,
                    "Updated Name",
                    deadline,
                    "Updated description"
            );
        });

        assertEquals(CourseErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(nonExistentTaskId.toString()));
    }

    @Test
    @DisplayName("UC-11.02: Fails to update task with non-existent course ID")
    public void testUpdateTaskWithNonExistentCourse() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetask.uc1102b@test.com",
                "Password123!"
        ));

        // Use random UUIDs that don't exist
        UUID nonExistentCourseId = UUID.randomUUID();
        UUID nonExistentTaskId = UUID.randomUUID();
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.updateTask(
                    nonExistentCourseId,
                    nonExistentTaskId,
                    "Updated Name",
                    deadline,
                    "Updated description"
            );
        });

        assertEquals(CourseErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(nonExistentCourseId.toString()));
    }
}

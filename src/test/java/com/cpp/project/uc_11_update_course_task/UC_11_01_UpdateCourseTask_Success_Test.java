package com.cpp.project.uc_11_update_course_task;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-11.01: Updates a course task with valid name, deadline, and description
 */
public class UC_11_01_UpdateCourseTask_Success_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-11.01: Updates task with all fields changed")
    public void testUpdateCourseTaskAllFields() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetask.uc1101@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Create a task
        Instant originalDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Original Task Name",
                originalDeadline,
                "Original description"
        );

        // Prepare updated values
        String updatedName = "Updated Task Name";
        String updatedDescription = "Updated description with more details";
        Instant updatedDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(2).toInstant();

        // Act
        CourseTaskDTO result = courseService.updateTask(
                course.getId(),
                task.getId(),
                updatedName,
                updatedDeadline,
                updatedDescription
        );

        // Assert
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(updatedName, result.getName());
        assertEquals(updatedDescription, result.getDescription());
        assertEquals(updatedDeadline, result.getDeadline());
        assertEquals(course.getId(), result.getCourseId());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    @DisplayName("UC-11.01: Updates only task name")
    public void testUpdateCourseTaskNameOnly() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetask.uc1101b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS102", "Data Structures", userId);

        // Create a task
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        String originalDescription = "Original description";
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Original Name",
                deadline,
                originalDescription
        );

        // Act - Update only the name
        String updatedName = "New Task Name";
        CourseTaskDTO result = courseService.updateTask(
                course.getId(),
                task.getId(),
                updatedName,
                deadline,
                originalDescription
        );

        // Assert
        assertNotNull(result);
        assertEquals(updatedName, result.getName());
        assertEquals(deadline, result.getDeadline());
        assertEquals(originalDescription, result.getDescription());
    }

    @Test
    @DisplayName("UC-11.01: Updates only task deadline")
    public void testUpdateCourseTaskDeadlineOnly() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetask.uc1101c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS103", "Algorithms", userId);

        // Create a task
        Instant originalDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        String taskName = "Assignment 1";
        String description = "Complete the assignment";
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                taskName,
                originalDeadline,
                description
        );

        // Act - Update only the deadline
        Instant newDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(7).toInstant();
        CourseTaskDTO result = courseService.updateTask(
                course.getId(),
                task.getId(),
                taskName,
                newDeadline,
                description
        );

        // Assert
        assertNotNull(result);
        assertEquals(taskName, result.getName());
        assertEquals(newDeadline, result.getDeadline());
        assertEquals(description, result.getDescription());
    }

    @Test
    @DisplayName("UC-11.01: Updates task description to empty string")
    public void testUpdateCourseTaskDescriptionToEmpty() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetask.uc1101d@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS104", "Software Engineering", userId);

        // Create a task with description
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Task with description",
                deadline,
                "Original description"
        );

        // Act - Update with empty description
        CourseTaskDTO result = courseService.updateTask(
                course.getId(),
                task.getId(),
                "Task with description",
                deadline,
                ""
        );

        // Assert
        assertNotNull(result);
        assertEquals("", result.getDescription());
    }
}

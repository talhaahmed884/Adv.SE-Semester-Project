package com.cpp.project.uc_12_delete_course_task;

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
 * UC-12.02: Fails to delete task when task is not found
 */
public class UC_12_02_DeleteCourseTask_Fail_TaskNotFound_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-12.02: Fails to delete task with non-existent task ID")
    public void testDeleteNonExistentTask() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetask.uc1202@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Use a random UUID that doesn't exist
        UUID nonExistentTaskId = UUID.randomUUID();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> courseService.deleteTask(course.getId(), nonExistentTaskId));

        assertEquals(CourseErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(nonExistentTaskId.toString()));
    }

    @Test
    @DisplayName("UC-12.02: Fails to delete task with non-existent course ID")
    public void testDeleteTaskWithNonExistentCourse() {
        // Arrange
        UUID nonExistentCourseId = UUID.randomUUID();
        UUID nonExistentTaskId = UUID.randomUUID();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> courseService.deleteTask(nonExistentCourseId, nonExistentTaskId));

        assertEquals(CourseErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(nonExistentCourseId.toString()));
    }

    @Test
    @DisplayName("UC-12.02: Fails to delete the same task twice")
    public void testDeleteTaskTwice() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetask.uc1202b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS102", "Data Structures", userId);

        // Add a task
        java.time.Instant deadline = java.time.ZonedDateTime.now(java.time.ZoneId.of("UTC")).plusDays(1).toInstant();
        com.cpp.project.course.dto.CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Task to delete",
                deadline,
                "This task will be deleted"
        );

        // Delete the task once
        courseService.deleteTask(course.getId(), task.getId());

        // Act & Assert - Try to delete again
        CourseException exception = assertThrows(CourseException.class, () -> courseService.deleteTask(course.getId(), task.getId()));

        assertEquals(CourseErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
    }
}

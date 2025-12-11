package com.cpp.project.uc_12_delete_course_task;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
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
 * UC-12.01: Deletes a course task successfully
 */
public class UC_12_01_DeleteCourseTask_Success_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-12.01: Deletes a single task from a course")
    public void testDeleteCourseTaskSuccess() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetask.uc1201@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Add a task
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Task to delete",
                deadline,
                "This task will be deleted"
        );

        // Verify task exists before deletion
        CourseDTO courseBefore = courseService.getCourseById(course.getId());
        assertEquals(1, courseBefore.getTasks().size());

        // Act
        courseService.deleteTask(course.getId(), task.getId());

        // Assert - Verify task is deleted
        CourseDTO courseAfter = courseService.getCourseById(course.getId());
        assertEquals(0, courseAfter.getTasks().size());
    }

    @Test
    @DisplayName("UC-12.01: Deletes one task while keeping others in the course")
    public void testDeleteOneTaskKeepOthers() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetask.uc1201b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS102", "Data Structures", userId);

        // Add multiple tasks
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        CourseTaskDTO task1 = courseService.addTaskToCourse(
                course.getId(),
                "Task 1 - Keep",
                deadline,
                "This task should remain"
        );
        CourseTaskDTO task2 = courseService.addTaskToCourse(
                course.getId(),
                "Task 2 - Delete",
                deadline,
                "This task will be deleted"
        );
        CourseTaskDTO task3 = courseService.addTaskToCourse(
                course.getId(),
                "Task 3 - Keep",
                deadline,
                "This task should remain"
        );

        // Verify all tasks exist before deletion
        CourseDTO courseBefore = courseService.getCourseById(course.getId());
        assertEquals(3, courseBefore.getTasks().size());

        // Act - Delete task2
        courseService.deleteTask(course.getId(), task2.getId());

        // Assert - Verify only task2 is deleted
        CourseDTO courseAfter = courseService.getCourseById(course.getId());
        assertEquals(2, courseAfter.getTasks().size());

        // Verify the correct tasks remain
        assertTrue(courseAfter.getTasks().stream()
                .anyMatch(t -> t.getId().equals(task1.getId())));
        assertTrue(courseAfter.getTasks().stream()
                .anyMatch(t -> t.getId().equals(task3.getId())));
        assertFalse(courseAfter.getTasks().stream()
                .anyMatch(t -> t.getId().equals(task2.getId())));
    }

    @Test
    @DisplayName("UC-12.01: Deletes a task with progress")
    public void testDeleteTaskWithProgress() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetask.uc1201c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS103", "Algorithms", userId);

        // Add a task
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Task with progress",
                deadline,
                "Task to be deleted after progress update"
        );

        // Update task progress
        courseService.updateTaskProgress(course.getId(), task.getId(), 50);

        // Act - Delete task with progress
        courseService.deleteTask(course.getId(), task.getId());

        // Assert - Verify task is deleted
        CourseDTO courseAfter = courseService.getCourseById(course.getId());
        assertEquals(0, courseAfter.getTasks().size());
    }

    @Test
    @DisplayName("UC-12.01: Attempting to retrieve deleted task throws exception")
    public void testDeletedTaskCannotBeAccessed() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetask.uc1201d@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS104", "Software Engineering", userId);

        // Add a task
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Task to delete",
                deadline,
                "This task will be deleted"
        );

        // Act - Delete task
        courseService.deleteTask(course.getId(), task.getId());

        // Assert - Attempting to update the deleted task should fail
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.updateTaskProgress(course.getId(), task.getId(), 50);
        });

        assertEquals(CourseErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
    }
}

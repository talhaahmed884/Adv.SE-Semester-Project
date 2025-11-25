package com.cpp.project.uc_5_update_course_progress;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-5.4.02: Sets progress mid (e.g., 55) -> IN_PROGRESS
 */
public class UC_5_4_02_UpdateProgress_Success_ToMiddle_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-5.4.02: Sets progress to middle value (55) and status=IN_PROGRESS")
    public void testUpdateProgressToMiddle() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc5402@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS102", "Data Structures", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Assignment 1",
                futureDeadline,
                "Complete the assignment"
        );

        // Act - Update progress to middle value
        CourseTaskDTO result = courseService.updateTaskProgress(course.getId(), task.getId(), 55);

        // Assert
        assertNotNull(result);
        assertEquals(55, result.getProgress());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    @DisplayName("UC-5.4.02: Sets progress to 1 (boundary) and status=IN_PROGRESS")
    public void testUpdateProgressToOne() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc5402b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS103", "Algorithms", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Assignment 1",
                futureDeadline,
                "Complete the assignment"
        );

        // Act - Update progress to 1 (boundary)
        CourseTaskDTO result = courseService.updateTaskProgress(course.getId(), task.getId(), 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getProgress());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    @DisplayName("UC-5.4.02: Sets progress to 99 (boundary) and status=IN_PROGRESS")
    public void testUpdateProgressToNinetyNine() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc5402c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS104", "Software Engineering", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Assignment 1",
                futureDeadline,
                "Complete the assignment"
        );

        // Act - Update progress to 99 (boundary)
        CourseTaskDTO result = courseService.updateTaskProgress(course.getId(), task.getId(), 99);

        // Assert
        assertNotNull(result);
        assertEquals(99, result.getProgress());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    }
}

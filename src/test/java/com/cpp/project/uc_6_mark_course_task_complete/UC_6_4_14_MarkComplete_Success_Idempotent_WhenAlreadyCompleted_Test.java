package com.cpp.project.uc_6_mark_course_task_complete;

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
 * UC-6.4.14: If already COMPLETED, calling markComplete is idempotent
 */
public class UC_6_4_14_MarkComplete_Success_Idempotent_WhenAlreadyCompleted_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-6.4.14: Mark complete when already COMPLETED is idempotent")
    public void testMarkCompleteIdempotentWhenAlreadyCompleted() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc6414@test.com",
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

        // Mark task as complete first time
        CourseTaskDTO firstComplete = courseService.markTaskComplete(course.getId(), task.getId());
        assertEquals(100, firstComplete.getProgress());
        assertEquals(TaskStatus.COMPLETED, firstComplete.getStatus());

        // Act - Mark task as complete again (already completed)
        CourseTaskDTO secondComplete = courseService.markTaskComplete(course.getId(), task.getId());

        // Assert - Should remain completed (idempotent)
        assertNotNull(secondComplete);
        assertEquals(100, secondComplete.getProgress());
        assertEquals(TaskStatus.COMPLETED, secondComplete.getStatus());
    }
}

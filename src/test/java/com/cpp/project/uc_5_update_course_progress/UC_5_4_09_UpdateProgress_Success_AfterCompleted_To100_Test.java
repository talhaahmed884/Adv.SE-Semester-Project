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
 * UC-5.4.09: After completion, setting 100 again is idempotent
 */
public class UC_5_4_09_UpdateProgress_Success_AfterCompleted_To100_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-5.4.09: Setting progress to 100 after completion is idempotent")
    public void testUpdateProgressAfterCompletedTo100() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc5409@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS111", "Distributed Systems", userId);

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

        // Mark task as complete (sets progress to 100 and status to COMPLETED)
        CourseTaskDTO completedTask = courseService.markTaskComplete(course.getId(), task.getId());
        assertEquals(100, completedTask.getProgress());
        assertEquals(TaskStatus.COMPLETED, completedTask.getStatus());

        // Act - Set progress to 100 again
        CourseTaskDTO result = courseService.updateTaskProgress(course.getId(), task.getId(), 100);

        // Assert - Should remain completed (idempotent)
        assertNotNull(result);
        assertEquals(100, result.getProgress());
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
    }
}

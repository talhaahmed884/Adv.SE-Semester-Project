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
 * UC-6.4.15: markComplete must not mutate deadline
 */
public class UC_6_4_15_MarkComplete_DoesNotAlterDeadline_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-6.4.15: Mark complete does not alter deadline")
    public void testMarkCompleteDoesNotAlterDeadline() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc6415@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS104", "Software Engineering", userId);

        // Create a specific future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date originalDeadline = calendar.getTime();

        // Add a task with the deadline
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Assignment 1",
                originalDeadline,
                "Complete the assignment"
        );

        // Verify initial deadline
        assertEquals(originalDeadline, task.getDeadline());

        // Act - Mark task as complete
        CourseTaskDTO result = courseService.markTaskComplete(course.getId(), task.getId());

        // Assert - Deadline should remain unchanged
        assertNotNull(result);
        assertEquals(100, result.getProgress());
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        assertEquals(originalDeadline, result.getDeadline());
    }
}

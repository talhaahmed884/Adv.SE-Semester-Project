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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-6.4.16: Executes in O(1) with no heavy work
 */
public class UC_6_4_16_MarkComplete_Performance_O1_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-6.4.16: Mark complete executes in O(1) with no heavy work")
    public void testMarkCompletePerformanceO1() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc6416@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS105", "Operating Systems", userId);

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

        // Act - Measure time to mark task as complete
        long startTime = System.currentTimeMillis();
        CourseTaskDTO result = courseService.markTaskComplete(course.getId(), task.getId());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert
        assertEquals(100, result.getProgress());
        assertEquals(TaskStatus.COMPLETED, result.getStatus());

        // Performance check - should execute very quickly (< 100ms)
        assertTrue(duration < 100, "markComplete took " + duration + "ms, expected < 100ms for O(1) operation");
    }
}

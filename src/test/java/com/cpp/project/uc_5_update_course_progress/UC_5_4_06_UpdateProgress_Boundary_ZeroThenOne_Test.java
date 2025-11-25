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
 * UC-5.4.06: Boundary bump 0+1 sets IN_PROGRESS
 */
public class UC_5_4_06_UpdateProgress_Boundary_ZeroThenOne_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-5.4.06: Boundary bump from 0 to 1 sets IN_PROGRESS")
    public void testUpdateProgressZeroThenOne() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc5406@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS108", "Artificial Intelligence", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task (starts at 0 by default)
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Assignment 1",
                futureDeadline,
                "Complete the assignment"
        );

        // Verify initial state
        assertEquals(0, task.getProgress());
        assertEquals(TaskStatus.PENDING, task.getStatus());

        // Act - Update progress from 0 to 1
        CourseTaskDTO result = courseService.updateTaskProgress(course.getId(), task.getId(), 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getProgress());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    }
}

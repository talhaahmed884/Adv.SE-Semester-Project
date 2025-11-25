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
 * UC-6.4.12: From TODO, markComplete sets progress=100 & COMPLETED
 */
public class UC_6_4_12_MarkComplete_Success_FromTodo_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-6.4.12: Mark complete from PENDING (TODO) sets progress=100 and status=COMPLETED")
    public void testMarkCompleteFromTodo() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc6412@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task (starts at progress=0, status=PENDING)
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Assignment 1",
                futureDeadline,
                "Complete the assignment"
        );

        // Verify initial state
        assertEquals(0, task.getProgress());
        assertEquals(TaskStatus.PENDING, task.getStatus());

        // Act - Mark task as complete
        CourseTaskDTO result = courseService.markTaskComplete(course.getId(), task.getId());

        // Assert
        assertNotNull(result);
        assertEquals(100, result.getProgress());
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
    }
}

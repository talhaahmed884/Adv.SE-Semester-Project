package com.cpp.project.uc_4_add_course_task;

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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-4.08: Rejects tasks whose deadline is in the past
 */
public class UC_4_08_AddCourseTask_Fail_PastDeadline_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-4.08: Rejects task with deadline in the past (1 day ago)")
    public void testAddCourseTaskWithPastDeadline() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc408@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Create a past deadline (1 day ago)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date pastDeadline = calendar.getTime();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.addTaskToCourse(
                    course.getId(),
                    "Late Task",
                    pastDeadline,
                    "Task with past deadline"
            );
        });

        assertTrue(exception.getMessage().toLowerCase().contains("deadline") ||
                exception.getMessage().toLowerCase().contains("past"));
        assertEquals(CourseErrorCode.INVALID_TASK_DEADLINE.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("UC-4.08: Rejects task with deadline 1ms in the past - boundary")
    public void testAddCourseTaskWithDeadlineJustPast() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc408b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS102", "Data Structures", userId);

        // Create a deadline 1ms in the past (boundary case)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -1);
        Date justPastDeadline = calendar.getTime();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.addTaskToCourse(
                    course.getId(),
                    "Barely Late Task",
                    justPastDeadline,
                    "Task with deadline just past"
            );
        });

        assertTrue(exception.getMessage().toLowerCase().contains("deadline") ||
                exception.getMessage().toLowerCase().contains("past"));
    }
}

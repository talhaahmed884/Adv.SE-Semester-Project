package com.cpp.project.uc_4_add_course_task;

import com.cpp.project.authentication.service.AuthenticationService;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-4.07: Adds a task with valid name, future deadline, and optional description
 */
public class UC_4_07_AddCourseTask_Success_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-4.07: Adds a task with valid name, future deadline, and description")
    public void testAddCourseTaskSuccess() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc407@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Create a future deadline (now + 1 day)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Act
        CourseTaskDTO result = courseService.addTaskToCourse(
                course.getId(),
                "Assignment 1",
                futureDeadline,
                "Complete the first assignment"
        );

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Assignment 1", result.getName());
        assertEquals("Complete the first assignment", result.getDescription());
        assertEquals(futureDeadline, result.getDeadline());
        assertEquals(0, result.getProgress());
    }

    @Test
    @DisplayName("UC-4.07: Adds a task with minimal deadline (now + 1 minute) - boundary")
    public void testAddCourseTaskWithMinimalFutureDeadline() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc407b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS102", "Data Structures", userId);

        // Create a deadline exactly 1 minute in the future (boundary case)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        Date minimalFutureDeadline = calendar.getTime();

        // Act
        CourseTaskDTO result = courseService.addTaskToCourse(
                course.getId(),
                "Quick Task",
                minimalFutureDeadline,
                "Task with minimal future deadline"
        );

        // Assert
        assertNotNull(result);
        assertEquals("Quick Task", result.getName());
        assertEquals(minimalFutureDeadline, result.getDeadline());
    }

    @Test
    @DisplayName("UC-4.07: Adds a task without description (optional)")
    public void testAddCourseTaskWithoutDescription() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc407c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS103", "Algorithms", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Act
        CourseTaskDTO result = courseService.addTaskToCourse(
                course.getId(),
                "Task without description",
                futureDeadline,
                null
        );

        // Assert
        assertNotNull(result);
        assertEquals("Task without description", result.getName());
        assertNull(result.getDescription());
    }
}

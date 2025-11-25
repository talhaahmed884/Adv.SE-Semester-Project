package com.cpp.project.uc_4_add_course_task;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
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
 * UC-4.1: Allows long descriptions up to 2000 chars; rejects longer
 */
public class UC_4_10_AddCourseTask_Success_LongDescription_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-4.1: Accepts task with empty description (0 chars) - boundary")
    public void testAddCourseTaskWithEmptyDescription() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc41a@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Act
        CourseTaskDTO result = courseService.addTaskToCourse(
                course.getId(),
                "Task with empty description",
                futureDeadline,
                ""
        );

        // Assert
        assertNotNull(result);
        assertEquals("Task with empty description", result.getName());
    }

    @Test
    @DisplayName("UC-4.1: Accepts task with 2000 character description - boundary")
    public void testAddCourseTaskWithMaxLengthDescription() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc41b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS102", "Data Structures", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Create a description with exactly 2000 characters
        String maxDescription = "A".repeat(2000);

        // Act
        CourseTaskDTO result = courseService.addTaskToCourse(
                course.getId(),
                "Task with max description",
                futureDeadline,
                maxDescription
        );

        // Assert
        assertNotNull(result);
        assertEquals("Task with max description", result.getName());
        assertEquals(2000, result.getDescription().length());
    }

    @Test
    @DisplayName("UC-4.1: Rejects task with 2001 character description - boundary")
    public void testAddCourseTaskWithTooLongDescription() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc41c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS103", "Algorithms", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Create a description with 2001 characters (exceeds limit)
        String tooLongDescription = "A".repeat(2001);

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.addTaskToCourse(
                    course.getId(),
                    "Task with too long description",
                    futureDeadline,
                    tooLongDescription
            );
        });

        assertTrue(exception.getMessage().toLowerCase().contains("description") ||
                exception.getMessage().toLowerCase().contains("length") ||
                exception.getMessage().toLowerCase().contains("maximum"));
    }
}

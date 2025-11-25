package com.cpp.project.uc_4_add_course_task;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-4.09: Null or blank task name is rejected
 */
public class UC_4_09_AddCourseTask_Fail_BlankName_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-4.09: Rejects task with null name")
    public void testAddCourseTaskWithNullName() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc409a@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS101", "Introduction to CS", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.addTaskToCourse(
                    course.getId(),
                    null,
                    futureDeadline,
                    "Task with null name"
            );
        });

        assertTrue(exception.getMessage().toLowerCase().contains("name") ||
                exception.getMessage().toLowerCase().contains("blank"));
    }

    @Test
    @DisplayName("UC-4.09: Rejects task with empty name")
    public void testAddCourseTaskWithEmptyName() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc409b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS102", "Data Structures", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.addTaskToCourse(
                    course.getId(),
                    "",
                    futureDeadline,
                    "Task with empty name"
            );
        });

        assertTrue(exception.getMessage().toLowerCase().contains("name") ||
                exception.getMessage().toLowerCase().contains("blank"));
    }

    @Test
    @DisplayName("UC-4.09: Rejects task with whitespace-only name")
    public void testAddCourseTaskWithWhitespaceName() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc409c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS103", "Algorithms", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.addTaskToCourse(
                    course.getId(),
                    "   ",
                    futureDeadline,
                    "Task with whitespace name"
            );
        });

        assertTrue(exception.getMessage().toLowerCase().contains("name") ||
                exception.getMessage().toLowerCase().contains("blank"));
    }
}

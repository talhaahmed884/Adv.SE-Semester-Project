package com.cpp.project.uc_3_get_course_progress;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-3.04: Validates rounding near thresholds (e.g., 2/3≈67%)
 */
public class UC_3_04_Progress_RoundingBoundaries_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.04: Progress rounds down with integer division (2/3 tasks)")
    public void testProgressRoundingTwoThirds() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc304a@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS104", "Software Engineering", userId);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDate = calendar.getTime();
        // Add 3 tasks
        CourseTaskDTO task1 = courseService.addTaskToCourse(course.getId(), "Task 1", futureDate, "Description 1");
        CourseTaskDTO task2 = courseService.addTaskToCourse(course.getId(), "Task 2", futureDate, "Description 2");
        courseService.addTaskToCourse(course.getId(), "Task 3", futureDate, "Description 3");

        // Mark 2 tasks as complete
        courseService.markTaskComplete(course.getId(), task1.getId());
        courseService.markTaskComplete(course.getId(), task2.getId());

        // Act
        CourseDTO result = courseService.getCourseById(course.getId());

        // Assert
        assertNotNull(result);
        // Progress = (100 + 100 + 0) / 3 = 200 / 3 = 66 (integer division truncates)
        assertEquals(66, result.getProgress());
    }

    @Test
    @DisplayName("UC-3.04: Progress rounds down with integer division (1/3 tasks)")
    public void testProgressRoundingOneThird() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc304b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS105", "Operating Systems", userId);

        // Add 3 tasks
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDate = calendar.getTime();
        CourseTaskDTO task1 = courseService.addTaskToCourse(course.getId(), "Task 1", futureDate, "Description 1");
        courseService.addTaskToCourse(course.getId(), "Task 2", futureDate, "Description 2");
        courseService.addTaskToCourse(course.getId(), "Task 3", futureDate, "Description 3");

        // Mark 1 task as complete
        courseService.markTaskComplete(course.getId(), task1.getId());

        // Act
        CourseDTO result = courseService.getCourseById(course.getId());

        // Assert
        assertNotNull(result);
        // Progress = (100 + 0 + 0) / 3 = 100 / 3 = 33 (integer division truncates)
        assertEquals(33, result.getProgress());
    }
}

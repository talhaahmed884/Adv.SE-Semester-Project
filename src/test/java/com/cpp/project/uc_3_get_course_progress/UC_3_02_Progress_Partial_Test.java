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

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-3.02: Returns percentage of completed tasks, rounded to nearest integer
 */
public class UC_3_02_Progress_Partial_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.02: Returns average progress when some tasks are completed")
    public void testProgressPartial() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc302@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS102", "Data Structures", userId);

        // Add 5 tasks and set 3 to 100% (completed) and 2 to 0%
        CourseTaskDTO task1 = courseService.addTaskToCourse(course.getId(), "Task 1", new Date(), "Description 1");
        CourseTaskDTO task2 = courseService.addTaskToCourse(course.getId(), "Task 2", new Date(), "Description 2");
        CourseTaskDTO task3 = courseService.addTaskToCourse(course.getId(), "Task 3", new Date(), "Description 3");
        courseService.addTaskToCourse(course.getId(), "Task 4", new Date(), "Description 4");
        courseService.addTaskToCourse(course.getId(), "Task 5", new Date(), "Description 5");

        // Mark 3 tasks as complete (100% progress)
        courseService.markTaskComplete(course.getId(), task1.getId());
        courseService.markTaskComplete(course.getId(), task2.getId());
        courseService.markTaskComplete(course.getId(), task3.getId());

        // Act
        CourseDTO result = courseService.getCourseById(course.getId());

        // Assert
        assertNotNull(result);
        // Progress = (100 + 100 + 100 + 0 + 0) / 5 = 300 / 5 = 60
        assertEquals(60, result.getProgress());
    }
}

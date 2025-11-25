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
 * UC-3.03: Returns 100 when all tasks are completed
 */
public class UC_3_03_Progress_AllCompleted_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.03: Returns 100 when all tasks are completed")
    public void testProgressAllCompleted() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc303@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS103", "Algorithms", userId);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDate = calendar.getTime();
        // Add 3 tasks
        CourseTaskDTO task1 = courseService.addTaskToCourse(course.getId(), "Task 1", futureDate, "Description 1");
        CourseTaskDTO task2 = courseService.addTaskToCourse(course.getId(), "Task 2", futureDate, "Description 2");
        CourseTaskDTO task3 = courseService.addTaskToCourse(course.getId(), "Task 3", futureDate, "Description 3");

        // Mark all tasks as complete
        courseService.markTaskComplete(course.getId(), task1.getId());
        courseService.markTaskComplete(course.getId(), task2.getId());
        courseService.markTaskComplete(course.getId(), task3.getId());

        // Act
        CourseDTO result = courseService.getCourseById(course.getId());

        // Assert
        assertNotNull(result);
        // Progress = (100 + 100 + 100) / 3 = 300 / 3 = 100
        assertEquals(100, result.getProgress());
    }
}

package com.cpp.project.uc_3_get_course_progress;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
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
 * UC-3.05: Computes progress efficiently with 10,000 tasks
 */
public class UC_3_05_Progress_Performance_10KTasks_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-3.05: Computes progress efficiently with 10,000 tasks")
    public void testProgressPerformanceWith10KTasks() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc305@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS106", "Large Scale Systems", userId);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDate = calendar.getTime();
        // Add 10,000 tasks
        int taskCount = 10000;
        for (int i = 0; i < taskCount; i++) {
            courseService.addTaskToCourse(course.getId(), "Task " + i, futureDate, "Description " + i);
        }

        // Mark half of them as complete
        CourseDTO courseWithTasks = courseService.getCourseById(course.getId());
        for (int i = 0; i < taskCount / 2; i++) {
            courseService.markTaskComplete(course.getId(), courseWithTasks.getTasks().get(i).getId());
        }

        // Act
        long startTime = System.currentTimeMillis();
        CourseDTO result = courseService.getCourseById(course.getId());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert
        // Progress = (5000 * 100 + 5000 * 0) / 10000 = 500000 / 10000 = 50
        assertEquals(50, result.getProgress());

        // Performance check - should complete in reasonable time (< 200ms)
        assertTrue(duration < 200, "Progress calculation took " + duration + "ms, expected < 200ms");
    }
}

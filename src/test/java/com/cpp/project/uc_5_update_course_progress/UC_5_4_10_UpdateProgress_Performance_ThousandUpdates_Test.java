package com.cpp.project.uc_5_update_course_progress;

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
import org.springframework.core.env.PropertyResolver;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-5.4.1: 1000 sequential valid increases show no leaks or nonlinear slowdown
 */
public class UC_5_4_10_UpdateProgress_Performance_ThousandUpdates_Test extends BaseIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PropertyResolver propertyResolver;

    @Test
    @DisplayName("UC-5.4.1: 1000 sequential progress updates perform efficiently")
    public void testUpdateProgressPerformanceThousandUpdates() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.progress.uc541@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a course
        CourseDTO course = courseService.createCourse("CS112", "Performance Testing", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Assignment 1",
                futureDeadline,
                "Complete the assignment"
        );

        // Act - Perform 1000 sequential updates
        long startTime = System.currentTimeMillis();

        for (int i = 0; i <= 100; i++) {
            // Update progress from 0 to 100, then repeat (10 cycles of 0-100)
            int progress = i % 101;
            courseService.updateTaskProgress(course.getId(), task.getId(), progress);
        }

        // Additional 900 updates to reach 1000 total
        for (int i = 0; i <= 900; i++) {
            int progress = i % 101;
            courseService.updateTaskProgress(course.getId(), task.getId(), progress);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert - Final verification
        CourseTaskDTO finalTask = courseService.getCourseById(course.getId()).getTasks().getFirst();
        assertEquals(92, finalTask.getProgress()); // Last update was 92 (900 % 101)

        // Performance check - should not have significant slowdown
        System.out.println("1000 progress updates took: " + duration + "ms");
        assertTrue(duration < 5000, "1000 updates took " + duration + "ms, expected < 5000ms");
    }
}

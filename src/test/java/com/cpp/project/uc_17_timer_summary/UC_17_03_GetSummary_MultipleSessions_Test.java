package com.cpp.project.uc_17_timer_summary;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-14.03: Get timer summary correctly counts multiple sessions
 */
public class UC_17_03_GetSummary_MultipleSessions_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-14.03: Summary correctly counts all sessions")
    public void testGetSummaryMultipleSessions() throws InterruptedException {
        // Arrange - Create user, course, and task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1403@test.com",
                "Password123!"
        ));

        CourseDTO course = courseService.createCourse("CS101", "Test Course", user.getId());

        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment"
        );

        // Create 5 completed sessions
        for (int i = 0; i < 5; i++) {
            TimerDTO session = timerService.startTimer(user.getId(), task.getId());
            Thread.sleep(50);
            timerService.stopTimer(session.getId(), user.getId());
        }

        // Act - Get summary
        TaskTimerSummaryDTO summary = timerService.getTimerSummaryByTaskId(task.getId());

        // Assert
        assertNotNull(summary);
        assertEquals(task.getId(), summary.getCourseTaskId());
        assertEquals(5, summary.getSessionCount(), "Should have exactly 5 sessions");
        assertTrue(summary.getTotalTimeMillis() > 0, "Should have accumulated time");
        assertNull(summary.getActiveSession(), "Should have no active session");

        // Verify sessions list
        assertNotNull(summary.getSessions());
        assertEquals(5, summary.getSessions().size());

        // Calculate total time manually and verify
        long manualTotal = summary.getSessions().stream()
                .mapToLong(TimerDTO::getDurationMillis)
                .sum();
        assertEquals(manualTotal, summary.getTotalTimeMillis(),
                "Total time should match sum of individual sessions");
    }
}

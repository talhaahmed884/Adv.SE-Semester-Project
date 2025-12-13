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
 * UC-14.02: Get timer summary with no active session
 */
public class UC_17_02_GetSummary_NoActiveSession_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-14.02: Get summary with all sessions stopped")
    public void testGetSummaryNoActiveSession() throws InterruptedException {
        // Arrange - Create user, course, and task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1402@test.com",
                "Password123!"
        ));

        CourseDTO course = courseService.createCourse("CS101", "Test Course", user.getId());

        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment"
        );

        // Create two completed sessions
        TimerDTO session1 = timerService.startTimer(user.getId(), task.getId());
        Thread.sleep(100);
        timerService.stopTimer(session1.getId(), user.getId());

        TimerDTO session2 = timerService.startTimer(user.getId(), task.getId());
        Thread.sleep(100);
        timerService.stopTimer(session2.getId(), user.getId());

        // Act - Get summary
        TaskTimerSummaryDTO summary = timerService.getTimerSummaryByTaskId(task.getId());

        // Assert
        assertNotNull(summary);
        assertEquals(task.getId(), summary.getCourseTaskId());
        assertEquals(2, summary.getSessionCount(), "Should have 2 sessions");
        assertTrue(summary.getTotalTimeMillis() > 0, "Should have accumulated time");

        // Verify no active session
        assertNull(summary.getActiveSession(), "Should have no active session");

        // Verify all sessions are stopped
        assertNotNull(summary.getSessions());
        assertEquals(2, summary.getSessions().size());
        summary.getSessions().forEach(timer -> {
            assertNotNull(timer.getEndTime(), "All sessions should be stopped");
            assertTrue(timer.getDurationMillis() > 0, "All sessions should have duration");
        });
    }
}

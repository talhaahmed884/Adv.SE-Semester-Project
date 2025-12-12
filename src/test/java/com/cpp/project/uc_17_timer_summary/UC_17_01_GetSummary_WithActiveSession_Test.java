package com.cpp.project.uc_17_timer_summary;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.dto.TaskTimerSummaryDTO;
import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.entity.TimerStatus;
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
 * UC-14.01: Get timer summary with active session
 */
public class UC_17_01_GetSummary_WithActiveSession_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-14.01: Get summary shows active session")
    public void testGetSummaryWithActiveSession() throws InterruptedException {
        // Arrange - Create user, course, and task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1401@test.com",
                "Password123!"
        ));

        CourseDTO course = courseService.createCourse("CS101", "Test Course", user.getId());

        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment"
        );

        // Create one completed session
        TimerDTO session1 = timerService.startTimer(user.getId(), task.getId());
        Thread.sleep(100);
        timerService.stopTimer(session1.getId(), user.getId());

        // Create one active session
        TimerDTO activeSession = timerService.startTimer(user.getId(), task.getId());

        // Act - Get summary
        TaskTimerSummaryDTO summary = timerService.getTimerSummaryByTaskId(task.getId());

        // Assert
        assertNotNull(summary);
        assertEquals(task.getId(), summary.getCourseTaskId());
        assertEquals(2, summary.getSessionCount(), "Should have 2 sessions total");
        assertTrue(summary.getTotalTimeMillis() > 0, "Should have time from stopped session");

        // Verify active session is present
        assertNotNull(summary.getActiveSession(), "Should have active session");
        assertEquals(activeSession.getId(), summary.getActiveSession().getId());
        assertEquals(TimerStatus.RUNNING, summary.getActiveSession().getStatus());
        assertNull(summary.getActiveSession().getEndTime());

        // Verify sessions list
        assertNotNull(summary.getSessions());
        assertEquals(2, summary.getSessions().size());
    }
}

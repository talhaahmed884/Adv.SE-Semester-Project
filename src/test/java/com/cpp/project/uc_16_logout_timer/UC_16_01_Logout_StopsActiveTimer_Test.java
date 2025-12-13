package com.cpp.project.uc_16_logout_timer;

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
 * UC-13.01: Logout automatically stops active timer
 */
public class UC_16_01_Logout_StopsActiveTimer_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-13.01: Logout stops active timer")
    public void testLogoutStopsActiveTimer() throws InterruptedException {
        // Arrange - Create user, course, and task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1301@test.com",
                "Password123!"
        ));

        CourseDTO course = courseService.createCourse("CS101", "Test Course", user.getId());

        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment"
        );

        // Start timer
        TimerDTO startedTimer = timerService.startTimer(user.getId(), task.getId());
        assertEquals(TimerStatus.RUNNING, startedTimer.getStatus());

        Thread.sleep(100);

        // Act - Logout (which stops all active timers)
        timerService.stopAllActiveTimersForUser(user.getId());

        // Assert - Verify timer is stopped
        TaskTimerSummaryDTO summary = timerService.getTimerSummaryByTaskId(task.getId());
        assertNull(summary.getActiveSession(), "Should have no active session after logout");
        assertEquals(1, summary.getSessionCount());
        System.out.println(summary.getTotalTimeMillis());
        assertTrue(summary.getTotalTimeMillis() > 0, "Should have accumulated time");

        // Verify the timer is actually stopped
        TimerDTO timer = timerService.getTimerById(startedTimer.getId());
        assertEquals(TimerStatus.STOPPED, timer.getStatus());
        assertNotNull(timer.getEndTime());
        assertTrue(timer.getDurationMillis() > 0);
    }
}

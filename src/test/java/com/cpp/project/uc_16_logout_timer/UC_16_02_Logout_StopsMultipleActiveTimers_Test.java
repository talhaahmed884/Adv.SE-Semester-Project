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
 * UC-13.02: Logout stops all active timers for different tasks
 */
public class UC_16_02_Logout_StopsMultipleActiveTimers_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-13.02: Logout stops multiple active timers")
    public void testLogoutStopsMultipleActiveTimers() throws InterruptedException {
        // Arrange - Create user, course, and multiple tasks
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1302@test.com",
                "Password123!"
        ));

        CourseDTO course = courseService.createCourse("CS101", "Test Course", user.getId());

        CourseTaskDTO task1 = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment 1"
        );

        CourseTaskDTO task2 = courseService.addTaskToCourse(
                course.getId(),
                "Homework 2",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment 2"
        );

        CourseTaskDTO task3 = courseService.addTaskToCourse(
                course.getId(),
                "Homework 3",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment 3"
        );

        // Start timers for all three tasks
        TimerDTO timer1 = timerService.startTimer(user.getId(), task1.getId());
        TimerDTO timer2 = timerService.startTimer(user.getId(), task2.getId());
        TimerDTO timer3 = timerService.startTimer(user.getId(), task3.getId());

        assertEquals(TimerStatus.RUNNING, timer1.getStatus());
        assertEquals(TimerStatus.RUNNING, timer2.getStatus());
        assertEquals(TimerStatus.RUNNING, timer3.getStatus());

        Thread.sleep(100);

        // Act - Logout (which stops all active timers)
        timerService.stopAllActiveTimersForUser(user.getId());

        // Assert - Verify all timers are stopped
        TaskTimerSummaryDTO summary1 = timerService.getTimerSummaryByTaskId(task1.getId());
        TaskTimerSummaryDTO summary2 = timerService.getTimerSummaryByTaskId(task2.getId());
        TaskTimerSummaryDTO summary3 = timerService.getTimerSummaryByTaskId(task3.getId());

        assertNull(summary1.getActiveSession(), "Task 1 should have no active session");
        assertNull(summary2.getActiveSession(), "Task 2 should have no active session");
        assertNull(summary3.getActiveSession(), "Task 3 should have no active session");

        // Verify all timers have accumulated time
        assertTrue(summary1.getTotalTimeMillis() > 0, "Task 1 should have accumulated time");
        assertTrue(summary2.getTotalTimeMillis() > 0, "Task 2 should have accumulated time");
        assertTrue(summary3.getTotalTimeMillis() > 0, "Task 3 should have accumulated time");

        // Verify timers are actually stopped
        TimerDTO stoppedTimer1 = timerService.getTimerById(timer1.getId());
        TimerDTO stoppedTimer2 = timerService.getTimerById(timer2.getId());
        TimerDTO stoppedTimer3 = timerService.getTimerById(timer3.getId());

        assertEquals(TimerStatus.STOPPED, stoppedTimer1.getStatus());
        assertEquals(TimerStatus.STOPPED, stoppedTimer2.getStatus());
        assertEquals(TimerStatus.STOPPED, stoppedTimer3.getStatus());
    }
}

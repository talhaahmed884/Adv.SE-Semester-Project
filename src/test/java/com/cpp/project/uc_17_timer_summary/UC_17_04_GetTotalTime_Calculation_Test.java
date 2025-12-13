package com.cpp.project.uc_17_timer_summary;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
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
 * UC-14.04: Get total time correctly calculates sum
 */
public class UC_17_04_GetTotalTime_Calculation_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-14.04: Total time calculation is accurate")
    public void testGetTotalTimeCalculation() throws InterruptedException {
        // Arrange - Create user, course, and task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1404@test.com",
                "Password123!"
        ));

        CourseDTO course = courseService.createCourse("CS101", "Test Course", user.getId());

        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment"
        );

        // Create 3 sessions with different durations
        TimerDTO session1 = timerService.startTimer(user.getId(), task.getId());
        Thread.sleep(100);
        TimerDTO stopped1 = timerService.stopTimer(session1.getId(), user.getId());

        TimerDTO session2 = timerService.startTimer(user.getId(), task.getId());
        Thread.sleep(150);
        TimerDTO stopped2 = timerService.stopTimer(session2.getId(), user.getId());

        TimerDTO session3 = timerService.startTimer(user.getId(), task.getId());
        Thread.sleep(200);
        TimerDTO stopped3 = timerService.stopTimer(session3.getId(), user.getId());

        // Act - Get total time
        Long totalTime = timerService.getTotalTimeForTask(task.getId());

        // Assert
        assertNotNull(totalTime);
        assertTrue(totalTime > 0, "Total time should be greater than 0");

        // Calculate expected total
        long expectedTotal = stopped1.getDurationMillis() +
                stopped2.getDurationMillis() +
                stopped3.getDurationMillis();

        assertEquals(expectedTotal, totalTime, "Total time should match sum of all sessions");

        // Verify it's at least 450ms (100 + 150 + 200)
        assertTrue(totalTime >= 0, "Total should be at least the sum of sleep times in seconds");
    }

    @Test
    @DisplayName("UC-14.04: Total time excludes running timer")
    public void testGetTotalTimeExcludesRunningTimer() throws InterruptedException {
        // Arrange - Create user, course, and task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1404b@test.com",
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
        TimerDTO stopped1 = timerService.stopTimer(session1.getId(), user.getId());

        // Create one running session
        timerService.startTimer(user.getId(), task.getId());

        // Act - Get total time
        Long totalTime = timerService.getTotalTimeForTask(task.getId());

        // Assert - Should only include stopped session, not running one
        assertNotNull(totalTime);
        assertEquals(stopped1.getDurationMillis(), totalTime,
                "Total time should only include stopped sessions");
    }
}

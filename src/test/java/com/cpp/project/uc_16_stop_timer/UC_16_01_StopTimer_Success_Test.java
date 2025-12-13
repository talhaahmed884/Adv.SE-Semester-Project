package com.cpp.project.uc_16_stop_timer;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
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
 * UC-12.01: Successfully stop a running timer and calculate duration
 */
public class UC_16_01_StopTimer_Success_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-12.01: Successfully stop a running timer")
    public void testStopTimerSuccess() throws InterruptedException {
        // Arrange - Create user, course, and task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1201@test.com",
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
        assertNotNull(startedTimer);
        assertEquals(TimerStatus.RUNNING, startedTimer.getStatus());

        // Wait a bit to ensure duration > 0
        Thread.sleep(100);

        // Act - Stop the timer
        TimerDTO stoppedTimer = timerService.stopTimer(startedTimer.getId(), user.getId());

        // Assert
        assertNotNull(stoppedTimer);
        assertEquals(startedTimer.getId(), stoppedTimer.getId());
        assertEquals(TimerStatus.STOPPED, stoppedTimer.getStatus());
        assertNotNull(stoppedTimer.getEndTime());
        assertTrue(stoppedTimer.getDurationMillis() > 0, "Duration should be greater than 0");
        assertTrue(stoppedTimer.getEndTime().isAfter(stoppedTimer.getStartTime()));
    }
}

package com.cpp.project.uc_15_start_timer;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.entity.TimerException;
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
 * UC-11.02: Cannot start a timer if one is already running for the task
 */
public class UC_15_02_StartTimer_Fail_AlreadyRunning_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-11.02: Cannot start timer when already running for task")
    public void testStartTimerFailAlreadyRunning() {
        // Arrange
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1102@test.com",
                "Password123!"
        ));

        CourseDTO course = courseService.createCourse("CS101", "Test Course", user.getId());

        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment"
        );

        // Start first timer
        timerService.startTimer(user.getId(), task.getId());

        // Act & Assert - Try to start second timer, should fail
        TimerException exception = assertThrows(TimerException.class, () -> timerService.startTimer(user.getId(), task.getId()));

        assertEquals("TIMER_002", exception.getCode());
        assertTrue(exception.getMessage().contains("already running"));
    }
}

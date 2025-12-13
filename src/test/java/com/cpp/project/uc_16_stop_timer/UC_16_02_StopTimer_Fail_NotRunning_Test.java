package com.cpp.project.uc_16_stop_timer;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.dto.TimerDTO;
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
 * UC-12.02: Cannot stop a timer that is not running
 */
public class UC_16_02_StopTimer_Fail_NotRunning_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-12.02: Cannot stop an already stopped timer")
    public void testStopTimerFailNotRunning() throws InterruptedException {
        // Arrange - Create user, course, and task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1202@test.com",
                "Password123!"
        ));

        CourseDTO course = courseService.createCourse("CS101", "Test Course", user.getId());

        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment"
        );

        // Start and stop timer
        TimerDTO startedTimer = timerService.startTimer(user.getId(), task.getId());
        Thread.sleep(100);
        TimerDTO stoppedTimer = timerService.stopTimer(startedTimer.getId(), user.getId());
        assertNotNull(stoppedTimer);

        // Act & Assert - Try to stop again
        TimerException exception = assertThrows(TimerException.class, () -> timerService.stopTimer(stoppedTimer.getId(), user.getId()));

        assertEquals("TIMER_003", exception.getCode());
        assertTrue(exception.getMessage().contains("not running"));
    }
}

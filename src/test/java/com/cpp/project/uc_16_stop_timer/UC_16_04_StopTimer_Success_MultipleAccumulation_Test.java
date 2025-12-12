package com.cpp.project.uc_16_stop_timer;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-12.04: Multiple timer sessions correctly accumulate total time
 */
public class UC_16_04_StopTimer_Success_MultipleAccumulation_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-12.04: Multiple start/stop sessions accumulate correctly")
    public void testMultipleSessionsAccumulation() throws InterruptedException {
        // Arrange - Create user, course, and task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1204@test.com",
                "Password123!"
        ));

        CourseDTO course = courseService.createCourse("CS101", "Test Course", user.getId());

        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment"
        );

        // Act - Start and stop timer 3 times
        TimerDTO session1 = timerService.startTimer(user.getId(), task.getId());
        Thread.sleep(100);
        TimerDTO stoppedSession1 = timerService.stopTimer(session1.getId(), user.getId());

        TimerDTO session2 = timerService.startTimer(user.getId(), task.getId());
        Thread.sleep(100);
        TimerDTO stoppedSession2 = timerService.stopTimer(session2.getId(), user.getId());

        TimerDTO session3 = timerService.startTimer(user.getId(), task.getId());
        Thread.sleep(100);
        TimerDTO stoppedSession3 = timerService.stopTimer(session3.getId(), user.getId());

        // Assert - Verify all sessions have duration
        assertTrue(stoppedSession1.getDurationMillis() > 0);
        assertTrue(stoppedSession2.getDurationMillis() > 0);
        assertTrue(stoppedSession3.getDurationMillis() > 0);

        // Verify total time is sum of all sessions
        Long totalTime = timerService.getTotalTimeForTask(task.getId());
        long expectedTotal = stoppedSession1.getDurationMillis() +
                stoppedSession2.getDurationMillis() +
                stoppedSession3.getDurationMillis();

        assertEquals(expectedTotal, totalTime);

        // Verify all sessions are returned
        List<TimerDTO> timers = timerService.getTimersByTaskId(task.getId());
        assertEquals(3, timers.size());
    }
}

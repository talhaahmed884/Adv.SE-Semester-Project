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
 * UC-12.03: Cannot stop a timer owned by another user
 */
public class UC_16_03_StopTimer_Fail_UnauthorizedAccess_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-12.03: Cannot stop timer owned by another user")
    public void testStopTimerFailUnauthorizedAccess() {
        // Arrange - Create two users
        UserDTO user1 = authenticationService.signUp(new SignUpRequestDTO(
                "User One",
                "user.one.uc1203@test.com",
                "Password123!"
        ));

        UserDTO user2 = authenticationService.signUp(new SignUpRequestDTO(
                "User Two",
                "user.two.uc1203@test.com",
                "Password123!"
        ));

        // User 1 creates course and task
        CourseDTO course = courseService.createCourse("CS101", "Test Course", user1.getId());

        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment"
        );

        // User 1 starts timer
        TimerDTO timer = timerService.startTimer(user1.getId(), task.getId());
        assertNotNull(timer);

        // Act & Assert - User 2 tries to stop User 1's timer
        TimerException exception = assertThrows(TimerException.class, () -> timerService.stopTimer(timer.getId(), user2.getId()));

        assertEquals("TIMER_009", exception.getCode());
        assertTrue(exception.getMessage().contains("not authorized"));
    }
}

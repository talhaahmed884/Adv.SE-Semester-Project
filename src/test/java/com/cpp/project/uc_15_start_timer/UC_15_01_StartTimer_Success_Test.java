package com.cpp.project.uc_15_start_timer;

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
 * UC-11.01: Start a timer for a course task
 */
public class UC_15_01_StartTimer_Success_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("UC-11.01: Start a timer for a course task successfully")
    public void testStartTimerSuccess() {
        // Arrange - Create test user, course, and task
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1101@test.com",
                "Password123!"
        ));

        CourseDTO course = courseService.createCourse("CS101", "Test Course", user.getId());

        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Homework 1",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Complete programming assignment"
        );

        // Act
        TimerDTO result = timerService.startTimer(user.getId(), task.getId());

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(user.getId(), result.getUserId());
        assertEquals(task.getId(), result.getCourseTaskId());
        assertEquals(com.cpp.project.timer.entity.TimerStatus.RUNNING, result.getStatus());
        assertNotNull(result.getStartTime());
        assertNull(result.getEndTime());
        assertEquals(0, result.getDurationMillis());
    }
}

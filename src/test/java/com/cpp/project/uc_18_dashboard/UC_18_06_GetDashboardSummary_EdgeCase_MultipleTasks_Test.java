package com.cpp.project.uc_18_dashboard;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.service.DashboardService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-18.06: Edge case - Multiple tasks in same course with multiple timers
 */
public class UC_18_06_GetDashboardSummary_EdgeCase_MultipleTasks_Test extends BaseIntegrationTest {
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TimerService timerService;

    private UserDTO testUser;

    @BeforeEach
    public void setUp() {
        testUser = authenticationService.signUp(new SignUpRequestDTO(
                "Multiple Tasks Test User",
                "multiple.tasks.uc1806@test.com",
                "Password123!"
        ));
    }

    @Test
    @DisplayName("UC-18.06: Aggregate time from multiple tasks in same course")
    public void testMultipleTasksInSameCourse() throws InterruptedException {
        // Arrange - Create one course with multiple tasks
        CourseDTO course = courseService.createCourse("CS101", "Test Course", testUser.getId());

        // Task 1 with timer
        CourseTaskDTO task1 = courseService.addTaskToCourse(
                course.getId(),
                "Task 1",
                Instant.now().plus(1, ChronoUnit.DAYS),
                "First task"
        );
        TimerDTO timer1 = timerService.startTimer(testUser.getId(), task1.getId());
        Thread.sleep(100);
        timerService.stopTimer(timer1.getId(), testUser.getId());

        // Task 2 with timer
        CourseTaskDTO task2 = courseService.addTaskToCourse(
                course.getId(),
                "Task 2",
                Instant.now().plus(2, ChronoUnit.DAYS),
                "Second task"
        );
        TimerDTO timer2 = timerService.startTimer(testUser.getId(), task2.getId());
        Thread.sleep(100);
        timerService.stopTimer(timer2.getId(), testUser.getId());

        // Task 3 with multiple timer sessions
        CourseTaskDTO task3 = courseService.addTaskToCourse(
                course.getId(),
                "Task 3",
                Instant.now().plus(3, ChronoUnit.DAYS),
                "Third task"
        );
        TimerDTO timer3a = timerService.startTimer(testUser.getId(), task3.getId());
        Thread.sleep(50);
        timerService.stopTimer(timer3a.getId(), testUser.getId());

        TimerDTO timer3b = timerService.startTimer(testUser.getId(), task3.getId());
        Thread.sleep(50);
        timerService.stopTimer(timer3b.getId(), testUser.getId());

        // Act
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(testUser.getId());

        // Assert
        assertEquals(1, result.getCourseStudyTimes().size());

        // Verify time is aggregated from all three tasks
        long totalTime = result.getCourseStudyTimes().get(0).getTotalMillis();
        assertTrue(totalTime >= 300, "Should aggregate time from all tasks in course");

        // Verify all 3 tasks appear in upcoming tasks
        assertEquals(3, result.getUpcomingTasks().size());
    }
}

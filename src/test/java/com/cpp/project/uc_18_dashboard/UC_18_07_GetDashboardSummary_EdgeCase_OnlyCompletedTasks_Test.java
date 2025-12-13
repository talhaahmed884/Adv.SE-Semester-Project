package com.cpp.project.uc_18_dashboard;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.dto.UpcomingTaskDTO;
import com.cpp.project.dashboard.service.DashboardService;
import com.cpp.project.entity.BaseIntegrationTest;
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
 * UC-18.07: Edge case - Completed tasks still show in upcoming if deadline not passed
 */
public class UC_18_07_GetDashboardSummary_EdgeCase_OnlyCompletedTasks_Test extends BaseIntegrationTest {
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    private UserDTO testUser;

    @BeforeEach
    public void setUp() {
        testUser = authenticationService.signUp(new SignUpRequestDTO(
                "Completed Tasks Test User",
                "completed.tasks.uc1807@test.com",
                "Password123!"
        ));
    }

    @Test
    @DisplayName("UC-18.07: Completed tasks with upcoming deadlines still appear in dashboard")
    public void testCompletedTasksWithUpcomingDeadlines() {
        // Arrange - Create course with completed task
        CourseDTO course = courseService.createCourse("CS101", "Test Course", testUser.getId());
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Completed Task",
                Instant.now().plus(2, ChronoUnit.DAYS), // Still upcoming
                "Already done"
        );

        // Mark task as complete
        courseService.markTaskComplete(course.getId(), task.getId());

        // Act
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(testUser.getId());

        // Assert - Completed task should still appear in upcoming tasks
        assertEquals(1, result.getUpcomingTasks().size());

        UpcomingTaskDTO upcomingTask = result.getUpcomingTasks().get(0);
        assertEquals("Completed Task", upcomingTask.getTaskName());
        assertEquals(TaskStatus.COMPLETED, upcomingTask.getStatus());
    }
}

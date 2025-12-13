package com.cpp.project.uc_18_dashboard;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.entity.CourseErrorCode;
import com.cpp.project.course.entity.CourseException;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UC-18.09: Edge case - Tasks with past deadlines are excluded
 */
public class UC_18_08_GetDashboardSummary_EdgeCase_PastDeadlines_Test extends BaseIntegrationTest {
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
                "Past Deadline Test User",
                "past.deadline.uc1809@test.com",
                "Password123!"
        ));
    }

    @Test
    @DisplayName("UC-18.09: Tasks with past deadlines are excluded from upcoming tasks")
    public void testPastDeadlines() {
        // Arrange
        CourseDTO course = courseService.createCourse("CS101", "Test Course", testUser.getId());

        // Task with past deadline (should NOT appear)
        CourseException exception = assertThrows(CourseException.class, () -> courseService.addTaskToCourse(
                course.getId(),
                "Overdue Task",
                Instant.now().minus(1, ChronoUnit.DAYS), // Yesterday
                "Already past deadline"
        ));
        assertEquals(CourseErrorCode.INVALID_TASK_DEADLINE, exception.getCode());

        // Task with future deadline (should appear)
        courseService.addTaskToCourse(
                course.getId(),
                "Upcoming Task",
                Instant.now().plus(2, ChronoUnit.DAYS),
                "Still upcoming"
        );

        // Act
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(testUser.getId());

        // Assert - Only future task should appear
        assertEquals(1, result.getUpcomingTasks().size());
        assertEquals("Upcoming Task", result.getUpcomingTasks().get(0).getTaskName());
    }
}

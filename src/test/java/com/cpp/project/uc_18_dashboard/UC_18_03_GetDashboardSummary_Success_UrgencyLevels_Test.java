package com.cpp.project.uc_18_dashboard;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.dto.UpcomingTaskDTO;
import com.cpp.project.dashboard.entity.TaskUrgency;
import com.cpp.project.dashboard.service.DashboardService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-18.03: Verify task urgency levels (TODAY, TOMORROW, THIS_WEEK)
 */
public class UC_18_03_GetDashboardSummary_Success_UrgencyLevels_Test extends BaseIntegrationTest {
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    private UserDTO testUser;
    private CourseDTO testCourse;

    @BeforeEach
    public void setUp() {
        testUser = authenticationService.signUp(new SignUpRequestDTO(
                "Urgency Test User",
                "urgency.test.uc1803@test.com",
                "Password123!"
        ));

        testCourse = courseService.createCourse("TEST101", "Test Course", testUser.getId());
    }

    @Test
    @DisplayName("UC-18.03: Verify task urgency levels are correctly calculated")
    public void testUrgencyLevels() {
        // Arrange - Create tasks with different deadlines
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        // Task due today (same date)
        Instant todayDeadline = now.withHour(23).withMinute(59).toInstant();
        CourseTaskDTO todayTask = courseService.addTaskToCourse(
                testCourse.getId(),
                "Task Due Today",
                todayDeadline,
                "Due today"
        );

        // Task due tomorrow
        Instant tomorrowDeadline = now.plusDays(1).withHour(12).toInstant();
        CourseTaskDTO tomorrowTask = courseService.addTaskToCourse(
                testCourse.getId(),
                "Task Due Tomorrow",
                tomorrowDeadline,
                "Due tomorrow"
        );

        // Task due in 5 days (within this week)
        Instant thisWeekDeadline = now.plusDays(5).withHour(14).toInstant();
        CourseTaskDTO thisWeekTask = courseService.addTaskToCourse(
                testCourse.getId(),
                "Task Due This Week",
                thisWeekDeadline,
                "Due in 5 days"
        );

        // Task due in 10 days (outside 7-day window, should not appear)
        Instant outsideWindowDeadline = now.plusDays(10).toInstant();
        courseService.addTaskToCourse(
                testCourse.getId(),
                "Task Due Later",
                outsideWindowDeadline,
                "Due in 10 days"
        );

        // Act
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(testUser.getId());

        // Assert
        List<UpcomingTaskDTO> upcomingTasks = result.getUpcomingTasks();

        // Should have 3 tasks (excluding the one outside 7-day window)
        assertEquals(3, upcomingTasks.size());

        // Find each task and verify urgency
        UpcomingTaskDTO todayTaskResult = upcomingTasks.stream()
                .filter(t -> t.getTaskName().equals("Task Due Today"))
                .findFirst()
                .orElse(null);
        assertNotNull(todayTaskResult);
        assertEquals(TaskUrgency.TODAY, todayTaskResult.getUrgency());

        UpcomingTaskDTO tomorrowTaskResult = upcomingTasks.stream()
                .filter(t -> t.getTaskName().equals("Task Due Tomorrow"))
                .findFirst()
                .orElse(null);
        assertNotNull(tomorrowTaskResult);
        assertEquals(TaskUrgency.TOMORROW, tomorrowTaskResult.getUrgency());

        UpcomingTaskDTO thisWeekTaskResult = upcomingTasks.stream()
                .filter(t -> t.getTaskName().equals("Task Due This Week"))
                .findFirst()
                .orElse(null);
        assertNotNull(thisWeekTaskResult);
        assertEquals(TaskUrgency.THIS_WEEK, thisWeekTaskResult.getUrgency());

        // Verify task outside window is not included
        boolean hasOutsideTask = upcomingTasks.stream()
                .anyMatch(t -> t.getTaskName().equals("Task Due Later"));
        assertFalse(hasOutsideTask, "Should not include tasks outside 7-day window");
    }
}

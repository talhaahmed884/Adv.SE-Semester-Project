package com.cpp.project.uc_18_dashboard;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.dashboard.dto.CourseStudyTimeDTO;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.dto.UpcomingTaskDTO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-18.05: Verify sorting order (courses by time desc, tasks by deadline asc)
 */
public class UC_18_05_GetDashboardSummary_Success_SortingOrder_Test extends BaseIntegrationTest {
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
                "Sorting Test User",
                "sorting.test.uc1805@test.com",
                "Password123!"
        ));
    }

    @Test
    @DisplayName("UC-18.05: Verify courses sorted by time (desc) and tasks sorted by deadline (asc)")
    public void testSortingOrder() throws InterruptedException {
        // Arrange - Create courses in random order with different study times
        CourseDTO courseA = courseService.createCourse("A101", "Course A", testUser.getId());
        CourseTaskDTO taskA1 = courseService.addTaskToCourse(
                courseA.getId(),
                "Task A1",
                Instant.now().plus(5, ChronoUnit.DAYS),
                "Due in 5 days"
        );

        // Course A: 100ms (medium)
        TimerDTO timerA = timerService.startTimer(testUser.getId(), taskA1.getId());
        Thread.sleep(100);
        timerService.stopTimer(timerA.getId(), testUser.getId());

        CourseDTO courseB = courseService.createCourse("B202", "Course B", testUser.getId());
        CourseTaskDTO taskB1 = courseService.addTaskToCourse(
                courseB.getId(),
                "Task B1",
                Instant.now().plus(2, ChronoUnit.DAYS),
                "Due in 2 days"
        );

        // Course B: 200ms (most)
        TimerDTO timerB1 = timerService.startTimer(testUser.getId(), taskB1.getId());
        Thread.sleep(100);
        timerService.stopTimer(timerB1.getId(), testUser.getId());

        TimerDTO timerB2 = timerService.startTimer(testUser.getId(), taskB1.getId());
        Thread.sleep(100);
        timerService.stopTimer(timerB2.getId(), testUser.getId());

        CourseDTO courseC = courseService.createCourse("C303", "Course C", testUser.getId());
        CourseTaskDTO taskC1 = courseService.addTaskToCourse(
                courseC.getId(),
                "Task C1",
                Instant.now().plus(1, ChronoUnit.DAYS),
                "Due tomorrow"
        );

        // Course C: 0ms (least/neglected)

        // Act
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(testUser.getId());

        // Assert - Verify course study times are sorted by time descending
        List<CourseStudyTimeDTO> courseStudyTimes = result.getCourseStudyTimes();
        assertEquals(3, courseStudyTimes.size());

        // First should be Course B (most time)
        assertEquals("B202", courseStudyTimes.get(0).getCourseCode());
        assertTrue(courseStudyTimes.get(0).getTotalMillis() >= 200);

        // Second should be Course A (medium time)
        assertEquals("A101", courseStudyTimes.get(1).getCourseCode());
        assertTrue(courseStudyTimes.get(1).getTotalMillis() >= 100);

        // Third should be Course C (no time)
        assertEquals("C303", courseStudyTimes.get(2).getCourseCode());
        assertEquals(0, courseStudyTimes.get(2).getTotalMillis());

        // Assert - Verify upcoming tasks are sorted by deadline ascending
        List<UpcomingTaskDTO> upcomingTasks = result.getUpcomingTasks();
        assertEquals(3, upcomingTasks.size());

        // First task should be C1 (due tomorrow - soonest)
        assertEquals("Task C1", upcomingTasks.get(0).getTaskName());

        // Second task should be B1 (due in 2 days)
        assertEquals("Task B1", upcomingTasks.get(1).getTaskName());

        // Third task should be A1 (due in 5 days - latest)
        assertEquals("Task A1", upcomingTasks.get(2).getTaskName());

        // Verify deadlines are in ascending order
        for (int i = 0; i < upcomingTasks.size() - 1; i++) {
            assertTrue(
                    upcomingTasks.get(i).getDeadline().isBefore(upcomingTasks.get(i + 1).getDeadline()),
                    "Tasks should be sorted by deadline ascending"
            );
        }
    }
}

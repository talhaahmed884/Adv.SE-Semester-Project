package com.cpp.project.uc_18_dashboard;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.dashboard.dto.CourseStudyTimeDTO;
import com.cpp.project.dashboard.dto.DashboardSummaryDTO;
import com.cpp.project.dashboard.dto.UpcomingTaskDTO;
import com.cpp.project.dashboard.entity.TaskUrgency;
import com.cpp.project.dashboard.service.DashboardService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.service.ToDoListService;
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
 * UC-18.01: Get dashboard summary with course study times and upcoming tasks
 */
public class UC_18_01_GetDashboardSummary_Success_WithData_Test extends BaseIntegrationTest {
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ToDoListService todoListService;

    @Autowired
    private TimerService timerService;

    private UserDTO testUser;

    @BeforeEach
    public void setUp() {
        // Create test user
        testUser = authenticationService.signUp(new SignUpRequestDTO(
                "Dashboard Test User",
                "dashboard.test.uc1801@test.com",
                "Password123!"
        ));
    }

    @Test
    @DisplayName("UC-18.01: Get dashboard summary with courses, timers, and upcoming tasks")
    public void testGetDashboardSummaryWithData() throws InterruptedException {
        // Arrange - Create courses with tasks and timers
        CourseDTO course1 = courseService.createCourse("CS101", "Algorithms", testUser.getId());
        CourseTaskDTO task1 = courseService.addTaskToCourse(
                course1.getId(),
                "Homework 1",
                Instant.now().plus(1, ChronoUnit.DAYS), // Due tomorrow
                "Complete assignment"
        );

        // Start and stop timer for task1 to accumulate time
        TimerDTO timer1 = timerService.startTimer(testUser.getId(), task1.getId());
        Thread.sleep(100); // Simulate 100ms of work
        timerService.stopTimer(timer1.getId(), testUser.getId());

        CourseDTO course2 = courseService.createCourse("CS202", "Data Structures", testUser.getId());
        CourseTaskDTO task2 = courseService.addTaskToCourse(
                course2.getId(),
                "Project",
                Instant.now().plus(5, ChronoUnit.DAYS), // Due in 5 days
                "Complete project"
        );

        // Create course with no timer (neglected course)
        CourseDTO course3 = courseService.createCourse("CS303", "Databases", testUser.getId());
        courseService.addTaskToCourse(
                course3.getId(),
                "Reading",
                Instant.now().plus(10, ChronoUnit.DAYS), // Due in 10 days (outside 7-day window)
                "Read chapter 5"
        );

        // Create todo list with upcoming task
        ToDoListDTO todoList = todoListService.createToDoList("Exam Prep", testUser.getId());
        ToDoListTaskDTO todoTask = todoListService.addTaskToList(
                todoList.getId(),
                "Study for Midterm",
                Instant.now().plus(3, ChronoUnit.DAYS) // Due in 3 days
        );

        // Act
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(testUser.getId());

        // Assert - Verify dashboard summary structure
        assertNotNull(result);
        assertNotNull(result.getCourseStudyTimes());
        assertNotNull(result.getUpcomingTasks());

        // Verify course study times (3 courses)
        List<CourseStudyTimeDTO> courseStudyTimes = result.getCourseStudyTimes();
        assertEquals(3, courseStudyTimes.size());

        // Verify courses are sorted by time (most studied first)
        CourseStudyTimeDTO firstCourse = courseStudyTimes.get(0);
        assertEquals("CS101", firstCourse.getCourseCode());
        assertEquals("Algorithms", firstCourse.getCourseName());
        assertTrue(firstCourse.getTotalMillis() > 0); // Has timer data
        assertTrue(firstCourse.getPercentage() > 0); // Has percentage

        // Verify neglected course (CS303 should have 0 time)
        boolean hasNeglectedCourse = courseStudyTimes.stream()
                .anyMatch(c -> c.getCourseCode().equals("CS303") && c.getTotalMillis() == 0);
        assertTrue(hasNeglectedCourse, "Should have at least one neglected course with 0 hours");

        // Verify total study time
        assertTrue(result.getTotalStudyTimeMillis() > 0);

        // Verify upcoming tasks (should have 3 tasks within 7 days)
        List<UpcomingTaskDTO> upcomingTasks = result.getUpcomingTasks();
        assertEquals(3, upcomingTasks.size());

        // Verify tasks are sorted by deadline (most urgent first)
        UpcomingTaskDTO firstTask = upcomingTasks.get(0);
        assertEquals("Homework 1", firstTask.getTaskName());
        assertEquals(TaskUrgency.TOMORROW, firstTask.getUrgency());
        assertEquals("COURSE", firstTask.getSourceType());
        assertEquals("Algorithms", firstTask.getSourceName());

        // Verify todo list task is included
        boolean hasTodoTask = upcomingTasks.stream()
                .anyMatch(t -> t.getTaskName().equals("Study for Midterm") &&
                              t.getSourceType().equals("TODO_LIST"));
        assertTrue(hasTodoTask, "Should include tasks from todo lists");

        // Verify all tasks have valid urgency levels
        assertTrue(upcomingTasks.stream().allMatch(t -> t.getUrgency() != null));
    }
}

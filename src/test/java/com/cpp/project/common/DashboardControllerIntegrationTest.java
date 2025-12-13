package com.cpp.project.common;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.dto.TimerDTO;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for DashboardController
 * Tests dashboard REST API endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DashboardControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void setup() throws InterruptedException {
        // Create test user
        testUser = authenticationService.signUp(new SignUpRequestDTO(
                "Dashboard API Test User",
                "dashboard.api.test@example.com",
                "StrongPass123!@#"
        ));

        // Create test courses with tasks and timers
        CourseDTO course1 = courseService.createCourse("CS101", "Algorithms", testUser.getId());
        CourseTaskDTO task1 = courseService.addTaskToCourse(
                course1.getId(),
                "Homework 1",
                Instant.now().plus(1, ChronoUnit.DAYS), // Due tomorrow
                "Complete assignment"
        );

        // Add timer data
        TimerDTO timer1 = timerService.startTimer(testUser.getId(), task1.getId());
        Thread.sleep(100);
        timerService.stopTimer(timer1.getId(), testUser.getId());

        CourseDTO course2 = courseService.createCourse("CS202", "Data Structures", testUser.getId());
        courseService.addTaskToCourse(
                course2.getId(),
                "Project",
                Instant.now().plus(5, ChronoUnit.DAYS), // Due in 5 days
                "Complete project"
        );

        // Create todo list with upcoming task
        ToDoListDTO todoList = todoListService.createToDoList("Exam Prep", testUser.getId());
        todoListService.addTaskToList(
                todoList.getId(),
                "Study for Midterm",
                Instant.now().plus(3, ChronoUnit.DAYS)
        );
    }

    @Test
    @DisplayName("GET /api/dashboard/user/{userId} - Success (200 OK)")
    public void testGetDashboardSummarySuccess() throws Exception {
        mockMvc.perform(get("/api/dashboard/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.courseStudyTimes").isArray())
                .andExpect(jsonPath("$.data.courseStudyTimes.length()").value(2))
                .andExpect(jsonPath("$.data.upcomingTasks").isArray())
                .andExpect(jsonPath("$.data.upcomingTasks.length()").value(3))
                .andExpect(jsonPath("$.data.totalStudyTimeMillis").exists())
                .andExpect(jsonPath("$.message").value("Dashboard summary retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/dashboard/user/{userId} - Verify course study times structure")
    public void testGetDashboardSummaryVerifyCourseStudyTimes() throws Exception {
        mockMvc.perform(get("/api/dashboard/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseStudyTimes[0].courseId").exists())
                .andExpect(jsonPath("$.data.courseStudyTimes[0].courseName").exists())
                .andExpect(jsonPath("$.data.courseStudyTimes[0].courseCode").exists())
                .andExpect(jsonPath("$.data.courseStudyTimes[0].totalMillis").exists())
                .andExpect(jsonPath("$.data.courseStudyTimes[0].percentage").exists())
                // Verify first course has timer data
                .andExpect(jsonPath("$.data.courseStudyTimes[0].courseCode").value("CS101"))
                .andExpect(jsonPath("$.data.courseStudyTimes[0].courseName").value("Algorithms"));
    }

    @Test
    @DisplayName("GET /api/dashboard/user/{userId} - Verify upcoming tasks structure")
    public void testGetDashboardSummaryVerifyUpcomingTasks() throws Exception {
        mockMvc.perform(get("/api/dashboard/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.upcomingTasks[0].taskId").exists())
                .andExpect(jsonPath("$.data.upcomingTasks[0].taskName").exists())
                .andExpect(jsonPath("$.data.upcomingTasks[0].deadline").exists())
                .andExpect(jsonPath("$.data.upcomingTasks[0].urgency").exists())
                .andExpect(jsonPath("$.data.upcomingTasks[0].sourceType").exists())
                .andExpect(jsonPath("$.data.upcomingTasks[0].sourceName").exists())
                .andExpect(jsonPath("$.data.upcomingTasks[0].status").exists())
                // Verify urgency values are valid
                .andExpect(jsonPath("$.data.upcomingTasks[0].urgency").value("TOMORROW"))
                .andExpect(jsonPath("$.data.upcomingTasks[0].taskName").value("Homework 1"));
    }

    @Test
    @DisplayName("GET /api/dashboard/user/{userId} - Empty data returns empty lists")
    public void testGetDashboardSummaryEmptyData() throws Exception {
        // Create new user with no data
        UserDTO emptyUser = authenticationService.signUp(new SignUpRequestDTO(
                "Empty User",
                "empty.user@example.com",
                "StrongPass123!@#"
        ));

        mockMvc.perform(get("/api/dashboard/user/" + emptyUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseStudyTimes").isArray())
                .andExpect(jsonPath("$.data.courseStudyTimes.length()").value(0))
                .andExpect(jsonPath("$.data.upcomingTasks").isArray())
                .andExpect(jsonPath("$.data.upcomingTasks.length()").value(0))
                .andExpect(jsonPath("$.data.totalStudyTimeMillis").value(0))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/dashboard/user/{userId} - Invalid UUID returns error")
    public void testGetDashboardSummaryInvalidUserId() throws Exception {
        mockMvc.perform(get("/api/dashboard/user/invalid-uuid"))
                .andExpect(status().isInternalServerError());
    }
}

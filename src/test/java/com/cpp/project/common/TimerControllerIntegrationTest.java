package com.cpp.project.common;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.CourseDTO;
import com.cpp.project.course.dto.CourseTaskDTO;
import com.cpp.project.course.service.CourseService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.dto.StartTimerRequestDTO;
import com.cpp.project.timer.dto.StopTimerRequestDTO;
import com.cpp.project.user.dto.LogoutRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for TimerController
 * Tests all timer REST API endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TimerControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;

    private UserDTO testUser;
    private UUID testTaskId;

    @BeforeEach
    public void setup() {
        // Create test user
        testUser = authenticationService.signUp(new SignUpRequestDTO(
                "Timer Test User",
                "timer.test@example.com",
                "StrongPass123!@#"
        ));

        // Create test course
        CourseDTO course = courseService.createCourse(
                "CS101",
                "Test Course",
                testUser.getId()
        );

        // Create test task
        CourseTaskDTO task = courseService.addTaskToCourse(
                course.getId(),
                "Test Task",
                Instant.now().plus(7, ChronoUnit.DAYS),
                "Test task for timer testing"
        );
        testTaskId = task.getId();
    }

    @Test
    @DisplayName("POST /api/timers/start - Success (201 Created)")
    public void testStartTimerSuccess() throws Exception {
        StartTimerRequestDTO request = new StartTimerRequestDTO(
                testUser.getId(),
                testTaskId
        );

        mockMvc.perform(post("/api/timers/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.data.courseTaskId").value(testTaskId.toString()))
                .andExpect(jsonPath("$.data.status").value("RUNNING"))
                .andExpect(jsonPath("$.data.durationMillis").value(0))
                .andExpect(jsonPath("$.data.endTime").doesNotExist())
                .andExpect(jsonPath("$.message").value("Timer started successfully"))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    @DisplayName("POST /api/timers/start - Already Running (409 Conflict)")
    public void testStartTimerAlreadyRunning() throws Exception {
        StartTimerRequestDTO request = new StartTimerRequestDTO(
                testUser.getId(),
                testTaskId
        );

        // Start first timer - success
        mockMvc.perform(post("/api/timers/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Start second timer - conflict
        mockMvc.perform(post("/api/timers/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TIMER_002"))
                .andExpect(jsonPath("$.statusCode").value(409));
    }

    @Test
    @DisplayName("POST /api/timers/start - Invalid Request (400 Bad Request)")
    public void testStartTimerInvalidRequest() throws Exception {
        StartTimerRequestDTO request = new StartTimerRequestDTO(null, null);

        mockMvc.perform(post("/api/timers/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TIMER_008"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("POST /api/timers/stop - Success (200 OK)")
    public void testStopTimerSuccess() throws Exception {
        // Start timer first
        StartTimerRequestDTO startRequest = new StartTimerRequestDTO(
                testUser.getId(),
                testTaskId
        );

        MvcResult startResult = mockMvc.perform(post("/api/timers/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String startResponse = startResult.getResponse().getContentAsString();
        String timerId = objectMapper.readTree(startResponse)
                .get("data").get("id").asText();

        // Sleep briefly to ensure duration > 0
        Thread.sleep(100);

        // Stop timer
        StopTimerRequestDTO stopRequest = new StopTimerRequestDTO(
                UUID.fromString(timerId),
                testUser.getId()
        );

        mockMvc.perform(post("/api/timers/stop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stopRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("STOPPED"))
                .andExpect(jsonPath("$.data.endTime").exists())
                .andExpect(jsonPath("$.message").value("Timer stopped successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("POST /api/timers/stop - Timer Not Found (404 Not Found)")
    public void testStopTimerNotFound() throws Exception {
        StopTimerRequestDTO request = new StopTimerRequestDTO(
                UUID.randomUUID(),
                testUser.getId()
        );

        mockMvc.perform(post("/api/timers/stop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TIMER_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("GET /api/timers/task/{taskId} - Success (200 OK)")
    public void testGetTimersByTaskId() throws Exception {
        // Start and stop a timer
        StartTimerRequestDTO startRequest = new StartTimerRequestDTO(
                testUser.getId(),
                testTaskId
        );

        MvcResult startResult = mockMvc.perform(post("/api/timers/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andReturn();

        String timerId = objectMapper.readTree(startResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Thread.sleep(100);

        StopTimerRequestDTO stopRequest = new StopTimerRequestDTO(
                UUID.fromString(timerId),
                testUser.getId()
        );

        mockMvc.perform(post("/api/timers/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stopRequest)));

        // Get timers for task
        mockMvc.perform(get("/api/timers/task/" + testTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseTaskId").value(testTaskId.toString()))
                .andExpect(jsonPath("$.message").value("Timers retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/timers/task/{taskId}/summary - Success (200 OK)")
    public void testGetTimerSummary() throws Exception {
        // Start and stop a timer to create a session
        StartTimerRequestDTO startRequest = new StartTimerRequestDTO(
                testUser.getId(),
                testTaskId
        );

        MvcResult startResult = mockMvc.perform(post("/api/timers/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andReturn();

        String timerId = objectMapper.readTree(startResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Thread.sleep(100);

        StopTimerRequestDTO stopRequest = new StopTimerRequestDTO(
                UUID.fromString(timerId),
                testUser.getId()
        );

        mockMvc.perform(post("/api/timers/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stopRequest)));

        // Get timer summary
        mockMvc.perform(get("/api/timers/task/" + testTaskId + "/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseTaskId").value(testTaskId.toString()))
                .andExpect(jsonPath("$.data.sessionCount").value(1))
                .andExpect(jsonPath("$.data.activeSession").doesNotExist())
                .andExpect(jsonPath("$.message").value("Timer summary retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/timers/task/{taskId}/total - Success (200 OK)")
    public void testGetTotalTimeForTask() throws Exception {
        // Start and stop a timer
        StartTimerRequestDTO startRequest = new StartTimerRequestDTO(
                testUser.getId(),
                testTaskId
        );

        MvcResult startResult = mockMvc.perform(post("/api/timers/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andReturn();

        String timerId = objectMapper.readTree(startResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Thread.sleep(100);

        StopTimerRequestDTO stopRequest = new StopTimerRequestDTO(
                UUID.fromString(timerId),
                testUser.getId()
        );

        mockMvc.perform(post("/api/timers/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stopRequest)));

        // Get total time
        mockMvc.perform(get("/api/timers/task/" + testTaskId + "/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNumber())
                .andExpect(jsonPath("$.message").value("Total time retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("POST /api/auth/logout - Stops Active Timers (200 OK)")
    public void testLogoutStopsActiveTimers() throws Exception {
        // Start a timer
        StartTimerRequestDTO startRequest = new StartTimerRequestDTO(
                testUser.getId(),
                testTaskId
        );

        mockMvc.perform(post("/api/timers/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andExpect(status().isCreated());

        // Logout
        LogoutRequestDTO logoutRequest = new LogoutRequestDTO(testUser.getId());

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Logout successful"))
                .andExpect(jsonPath("$.message").value("All active timers stopped"))
                .andExpect(jsonPath("$.statusCode").value(200));

        // Verify timer summary shows no active session
        mockMvc.perform(get("/api/timers/task/" + testTaskId + "/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activeSession").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/logout - No Active Timers (200 OK)")
    public void testLogoutWithNoActiveTimers() throws Exception {
        LogoutRequestDTO request = new LogoutRequestDTO(testUser.getId());

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Logout successful"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}

package com.cpp.project.common;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.AddTaskRequestDTO;
import com.cpp.project.course.dto.CreateCourseRequestDTO;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.dto.AddToDoListTaskRequestDTO;
import com.cpp.project.todolist.dto.CreateToDoListRequestDTO;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for CalendarController
 * Tests calendar aggregation from Course and ToDoList tasks
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CalendarControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationService authenticationService;

    private UUID testUserId;

    @BeforeEach
    public void setUp() {
        // Create a test user for calendar operations
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "calendar.test" + System.currentTimeMillis() + "@example.com",
                "Password123!"
        ));
        testUserId = user.getId();
    }

    @Test
    @DisplayName("GET /api/calendar/items - Success with no items (200 OK)")
    public void testGetCalendarItemsEmpty() throws Exception {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        int year = nowUTC.getYear();
        int month = nowUTC.getMonthValue();

        mockMvc.perform(get("/api/calendar/items")
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month))
                        .param("userId", testUserId.toString()).param("timezone", "UTC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value("Calendar items retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/calendar/items - Success with course tasks (200 OK)")
    public void testGetCalendarItemsWithCourseTasks() throws Exception {
        // Create a course
        CreateCourseRequestDTO courseRequest = new CreateCourseRequestDTO(
                "CS101",
                "Introduction to CS",
                testUserId
        );

        MvcResult courseResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(courseResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Add a task with deadline in current month
        Instant taskDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(5).toInstant();

        AddTaskRequestDTO taskRequest = new AddTaskRequestDTO(
                "Assignment 1",
                taskDeadline,
                "Complete the assignment"
        );

        mockMvc.perform(post("/api/courses/" + courseId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated());

        // Get calendar items for current month
        ZonedDateTime taskDateTime = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(5);
        int year = taskDateTime.getYear();
        int month = taskDateTime.getMonthValue();

        mockMvc.perform(get("/api/calendar/items")
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month))
                        .param("userId", testUserId.toString()).param("timezone", "UTC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].sourceType").value("COURSE"))
                .andExpect(jsonPath("$.data[0].title").value("Assignment 1"))
                .andExpect(jsonPath("$.message").value("Calendar items retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/calendar/items - Success with todo list tasks (200 OK)")
    public void testGetCalendarItemsWithTodoTasks() throws Exception {
        // Create a todo list
        CreateToDoListRequestDTO todoListRequest = new CreateToDoListRequestDTO(
                "My Tasks",
                testUserId
        );

        MvcResult todoListResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoListRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(todoListResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Add a task with deadline in current month
        Instant taskDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(3).toInstant();

        AddToDoListTaskRequestDTO taskRequest = new AddToDoListTaskRequestDTO(
                "Buy groceries",
                taskDeadline
        );

        mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated());

        // Get calendar items for current month
        ZonedDateTime taskDateTime = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(3);
        int year = taskDateTime.getYear();
        int month = taskDateTime.getMonthValue();

        mockMvc.perform(get("/api/calendar/items")
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month))
                        .param("userId", testUserId.toString()).param("timezone", "UTC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].sourceType").value("TODO_LIST"))
                .andExpect(jsonPath("$.data[0].title").value("Buy groceries"))
                .andExpect(jsonPath("$.message").value("Calendar items retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/calendar/items - Success with mixed tasks (200 OK)")
    public void testGetCalendarItemsWithMixedTasks() throws Exception {
        // Create a course with a task
        CreateCourseRequestDTO courseRequest = new CreateCourseRequestDTO(
                "CS202",
                "Data Structures",
                testUserId
        );

        MvcResult courseResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(courseResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Instant courseTaskDeadline = ZonedDateTime.of(2026, 5, 8, 0, 0, 0, 0, ZoneId.of("UTC"))
                .plusDays(7)
                .toInstant();

        AddTaskRequestDTO courseTaskRequest = new AddTaskRequestDTO(
                "Homework 1",
                courseTaskDeadline,
                "Complete homework"
        );

        mockMvc.perform(post("/api/courses/" + courseId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseTaskRequest)))
                .andExpect(status().isCreated());

        // Create a todo list with a task
        CreateToDoListRequestDTO todoListRequest = new CreateToDoListRequestDTO(
                "Personal Tasks",
                testUserId
        );

        MvcResult todoListResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoListRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(todoListResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Instant todoTaskDeadline = ZonedDateTime.of(2026, 5, 13, 0, 0, 0, 0, ZoneId.of("UTC"))
                .plusDays(2)
                .toInstant();

        AddToDoListTaskRequestDTO todoTaskRequest = new AddToDoListTaskRequestDTO(
                "Call dentist",
                todoTaskDeadline
        );

        mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoTaskRequest)))
                .andExpect(status().isCreated());

        // Get calendar items for current month
        ZonedDateTime taskDateTime = ZonedDateTime.of(2026, 5, 8, 0, 0, 0, 0, ZoneId.of("UTC")).plusDays(7);
        int year = taskDateTime.getYear();
        int month = taskDateTime.getMonthValue();

        mockMvc.perform(get("/api/calendar/items")
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month))
                        .param("userId", testUserId.toString()).param("timezone", "UTC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.message").value("Calendar items retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/calendar/user/{userId}/items - Success (200 OK)")
    public void testGetCalendarItemsByUserPath() throws Exception {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        int year = nowUTC.getYear();
        int month = nowUTC.getMonthValue();

        mockMvc.perform(get("/api/calendar/user/" + testUserId + "/items")
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month)).param("timezone", "UTC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Calendar items retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/calendar/items - Invalid month (400 Bad Request)")
    public void testGetCalendarItemsInvalidMonth() throws Exception {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        int year = nowUTC.getYear();

        mockMvc.perform(get("/api/calendar/items")
                        .param("year", String.valueOf(year))
                        .param("month", "13") // Invalid month
                        .param("userId", testUserId.toString()).param("timezone", "UTC"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CALENDAR_001"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("GET /api/calendar/items - Invalid year (400 Bad Request)")
    public void testGetCalendarItemsInvalidYear() throws Exception {
        mockMvc.perform(get("/api/calendar/items")
                        .param("year", "1800") // Invalid year
                        .param("month", "1")
                        .param("userId", testUserId.toString()).param("timezone", "UTC"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CALENDAR_001"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("GET /api/calendar/items - Filters tasks outside month (200 OK)")
    public void testGetCalendarItemsFiltersByMonth() throws Exception {
        // Create a course with a task in next month
        CreateCourseRequestDTO courseRequest = new CreateCourseRequestDTO(
                "CS303",
                "Algorithms",
                testUserId
        );

        MvcResult courseResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(courseResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Add task with deadline in NEXT month
        Instant nextMonthDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusMonths(1).toInstant();

        AddTaskRequestDTO taskRequest = new AddTaskRequestDTO(
                "Future Assignment",
                nextMonthDeadline,
                "Due next month"
        );

        mockMvc.perform(post("/api/courses/" + courseId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated());

        // Query for CURRENT month - should not return the future task
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        int currentYear = currentDateTime.getYear();
        int currentMonth = currentDateTime.getMonthValue();

        mockMvc.perform(get("/api/calendar/items")
                        .param("year", String.valueOf(currentYear))
                        .param("month", String.valueOf(currentMonth))
                        .param("userId", testUserId.toString()).param("timezone", "UTC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}

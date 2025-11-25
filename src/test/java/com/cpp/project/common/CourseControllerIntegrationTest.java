package com.cpp.project.common;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.course.dto.AddTaskRequestDTO;
import com.cpp.project.course.dto.CreateCourseRequestDTO;
import com.cpp.project.course.dto.UpdateCourseRequestDTO;
import com.cpp.project.course.dto.UpdateTaskProgressRequestDTO;
import com.cpp.project.entity.BaseIntegrationTest;
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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for CourseController
 * Tests all REST endpoints for course and course task operations
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationService authenticationService;

    private UUID testUserId;

    @BeforeEach
    public void setUp() {
        // Create a test user for course operations
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "course.controller.test" + System.currentTimeMillis() + "@example.com",
                "Password123!"
        ));
        testUserId = user.getId();
    }

    @Test
    @DisplayName("POST /api/courses - Success (201 Created)")
    public void testCreateCourseSuccess() throws Exception {
        CreateCourseRequestDTO request = new CreateCourseRequestDTO(
                "CS101",
                "Introduction to Computer Science",
                testUserId
        );

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code").value("CS101"))
                .andExpect(jsonPath("$.data.name").value("Introduction to Computer Science"))
                .andExpect(jsonPath("$.message").value("Course created successfully"))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    @DisplayName("POST /api/courses - Validation Error (400 Bad Request)")
    public void testCreateCourseValidationError() throws Exception {
        CreateCourseRequestDTO request = new CreateCourseRequestDTO(
                null, // Invalid code
                null, // Invalid name
                null  // Invalid userId
        );

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COURSE_VAL_001"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("POST /api/courses - Duplicate Code (409 Conflict)")
    public void testCreateCourseDuplicateCode() throws Exception {
        CreateCourseRequestDTO request = new CreateCourseRequestDTO(
                "CS202",
                "Data Structures",
                testUserId
        );

        // First creation - success
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second creation with same code - conflict
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("COURSE_002"))
                .andExpect(jsonPath("$.statusCode").value(409));
    }

    @Test
    @DisplayName("GET /api/courses/{id} - Success (200 OK)")
    public void testGetCourseByIdSuccess() throws Exception {
        // Create a course first
        CreateCourseRequestDTO createRequest = new CreateCourseRequestDTO(
                "CS303",
                "Algorithms",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Get the course by ID
        mockMvc.perform(get("/api/courses/" + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("CS303"))
                .andExpect(jsonPath("$.data.name").value("Algorithms"))
                .andExpect(jsonPath("$.message").value("Course retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/courses/{id} - Not Found (404)")
    public void testGetCourseByIdNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/courses/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("COURSE_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("GET /api/courses/code/{code} - Success (200 OK)")
    public void testGetCourseByCodeSuccess() throws Exception {
        // Create a course first
        CreateCourseRequestDTO createRequest = new CreateCourseRequestDTO(
                "CS404",
                "Operating Systems",
                testUserId
        );

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // Get the course by code
        mockMvc.perform(get("/api/courses/code/CS404"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("CS404"))
                .andExpect(jsonPath("$.data.name").value("Operating Systems"))
                .andExpect(jsonPath("$.message").value("Course retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/courses/user/{userId} - Success (200 OK)")
    public void testGetCoursesByUserIdSuccess() throws Exception {
        // Create multiple courses for the user
        CreateCourseRequestDTO request1 = new CreateCourseRequestDTO(
                "CS501",
                "Machine Learning",
                testUserId
        );
        CreateCourseRequestDTO request2 = new CreateCourseRequestDTO(
                "CS502",
                "Artificial Intelligence",
                testUserId
        );

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Get all courses for the user
        mockMvc.perform(get("/api/courses/user/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Courses retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("PUT /api/courses/{id} - Success (200 OK)")
    public void testUpdateCourseSuccess() throws Exception {
        // Create a course first
        CreateCourseRequestDTO createRequest = new CreateCourseRequestDTO(
                "CS603",
                "Old Name",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Update the course
        UpdateCourseRequestDTO updateRequest = new UpdateCourseRequestDTO("Updated Name");

        mockMvc.perform(put("/api/courses/" + courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.message").value("Course updated successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("DELETE /api/courses/{id} - Success (200 OK)")
    public void testDeleteCourseSuccess() throws Exception {
        // Create a course first
        CreateCourseRequestDTO createRequest = new CreateCourseRequestDTO(
                "CS704",
                "To Be Deleted",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Delete the course
        mockMvc.perform(delete("/api/courses/" + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deletion successful"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("POST /api/courses/{courseId}/tasks - Success (201 Created)")
    public void testAddTaskToCourseSuccess() throws Exception {
        // Create a course first
        CreateCourseRequestDTO createRequest = new CreateCourseRequestDTO(
                "CS805",
                "Software Engineering",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Add a task to the course
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date futureDeadline = calendar.getTime();

        AddTaskRequestDTO taskRequest = new AddTaskRequestDTO(
                "Complete Assignment 1",
                futureDeadline,
                "Implement the project requirements"
        );

        mockMvc.perform(post("/api/courses/" + courseId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Complete Assignment 1"))
                .andExpect(jsonPath("$.message").value("Task added successfully"))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    @DisplayName("PUT /api/courses/{courseId}/tasks/{taskId}/progress - Success (200 OK)")
    public void testUpdateTaskProgressSuccess() throws Exception {
        // Create a course and add a task
        CreateCourseRequestDTO createRequest = new CreateCourseRequestDTO(
                "CS906",
                "Database Systems",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date futureDeadline = calendar.getTime();

        AddTaskRequestDTO taskRequest = new AddTaskRequestDTO(
                "Study for Exam",
                futureDeadline,
                "Review all chapters"
        );

        MvcResult taskResult = mockMvc.perform(post("/api/courses/" + courseId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String taskId = objectMapper.readTree(taskResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Update task progress
        UpdateTaskProgressRequestDTO progressRequest = new UpdateTaskProgressRequestDTO(50);

        mockMvc.perform(put("/api/courses/" + courseId + "/tasks/" + taskId + "/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(progressRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.progress").value(50))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.message").value("Task progress updated successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("PUT /api/courses/{courseId}/tasks/{taskId}/complete - Success (200 OK)")
    public void testMarkTaskCompleteSuccess() throws Exception {
        // Create a course and add a task
        CreateCourseRequestDTO createRequest = new CreateCourseRequestDTO(
                "CS1007",
                "Computer Networks",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String courseId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date futureDeadline = calendar.getTime();

        AddTaskRequestDTO taskRequest = new AddTaskRequestDTO(
                "Network Lab",
                futureDeadline,
                "Complete lab exercises"
        );

        MvcResult taskResult = mockMvc.perform(post("/api/courses/" + courseId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String taskId = objectMapper.readTree(taskResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Mark task as complete
        mockMvc.perform(put("/api/courses/" + courseId + "/tasks/" + taskId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.progress").value(100))
                .andExpect(jsonPath("$.message").value("Task marked as complete"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}

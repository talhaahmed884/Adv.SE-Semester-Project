package com.cpp.project.common;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.dto.AddToDoListTaskRequestDTO;
import com.cpp.project.todolist.dto.CreateToDoListRequestDTO;
import com.cpp.project.todolist.dto.UpdateToDoListRequestDTO;
import com.cpp.project.todolist.dto.UpdateToDoListTaskRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for ToDoListController
 * Tests all REST endpoints for todo list and task operations
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ToDoListControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationService authenticationService;

    private UUID testUserId;

    @BeforeEach
    public void setUp() {
        // Create a test user for todo list operations
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "todolist.controller.test" + System.currentTimeMillis() + "@example.com",
                "Password123!"
        ));
        testUserId = user.getId();
    }

    @Test
    @DisplayName("POST /api/todolists - Success (201 Created)")
    public void testCreateToDoListSuccess() throws Exception {
        CreateToDoListRequestDTO request = new CreateToDoListRequestDTO(
                "My Todo List",
                testUserId
        );

        mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("My Todo List"))
                .andExpect(jsonPath("$.message").value("Todo list created successfully"))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    @DisplayName("POST /api/todolists - Validation Error (400 Bad Request)")
    public void testCreateToDoListValidationError() throws Exception {
        CreateToDoListRequestDTO request = new CreateToDoListRequestDTO(
                null, // Invalid name
                null  // Invalid userId
        );

        mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TODO_VAL_001"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("GET /api/todolists/{id} - Success (200 OK)")
    public void testGetToDoListByIdSuccess() throws Exception {
        // Create a todo list first
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Shopping List",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Get the todo list by ID
        mockMvc.perform(get("/api/todolists/" + todoListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Shopping List"))
                .andExpect(jsonPath("$.message").value("Todo list retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/todolists/{id} - Not Found (404)")
    public void testGetToDoListByIdNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/todolists/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("GET /api/todolists/user/{userId} - Success (200 OK)")
    public void testGetToDoListsByUserIdSuccess() throws Exception {
        // Create multiple todo lists for the user
        CreateToDoListRequestDTO request1 = new CreateToDoListRequestDTO(
                "Work Tasks",
                testUserId
        );
        CreateToDoListRequestDTO request2 = new CreateToDoListRequestDTO(
                "Personal Tasks",
                testUserId
        );

        mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Get all todo lists for the user
        mockMvc.perform(get("/api/todolists/user/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Todo lists retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("PUT /api/todolists/{id} - Success (200 OK)")
    public void testUpdateToDoListSuccess() throws Exception {
        // Create a todo list first
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Old Name",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Update the todo list
        UpdateToDoListRequestDTO updateRequest = new UpdateToDoListRequestDTO("Updated Name");

        mockMvc.perform(put("/api/todolists/" + todoListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.message").value("Todo list updated successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("DELETE /api/todolists/{id} - Success (200 OK)")
    public void testDeleteToDoListSuccess() throws Exception {
        // Create a todo list first
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "To Be Deleted",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Delete the todo list
        mockMvc.perform(delete("/api/todolists/" + todoListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deletion successful"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("POST /api/todolists/{todoListId}/tasks - Success (201 Created)")
    public void testAddTaskToListSuccess() throws Exception {
        // Create a todo list first
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Daily Tasks",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Add a task to the todo list
        Instant futureDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(3).toInstant();

        AddToDoListTaskRequestDTO taskRequest = new AddToDoListTaskRequestDTO(
                "Buy groceries",
                futureDeadline
        );

        mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.description").value("Buy groceries"))
                .andExpect(jsonPath("$.message").value("Task added successfully"))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    @DisplayName("POST /api/todolists/{todoListId}/tasks - Validation Error (400 Bad Request)")
    public void testAddTaskToListValidationError() throws Exception {
        // Create a todo list first
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "My List",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Try to add a task with invalid data
        AddToDoListTaskRequestDTO taskRequest = new AddToDoListTaskRequestDTO(
                null, // Invalid description
                null  // Invalid deadline
        );

        mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TODO_VAL_002"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("PUT /api/todolists/{todoListId}/tasks/{taskId}/complete - Success (200 OK)")
    public void testMarkTaskCompleteSuccess() throws Exception {
        // Create a todo list and add a task
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Project Tasks",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Instant futureDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(5).toInstant();

        AddToDoListTaskRequestDTO taskRequest = new AddToDoListTaskRequestDTO(
                "Complete documentation",
                futureDeadline
        );

        MvcResult taskResult = mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String taskId = objectMapper.readTree(taskResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Mark task as complete
        mockMvc.perform(put("/api/todolists/" + todoListId + "/tasks/" + taskId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.message").value("Task marked as complete"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/todolists/{todoListId}/deadlines - Success (200 OK)")
    public void testGetAggregatedDeadlinesSuccess() throws Exception {
        // Create a todo list and add multiple tasks
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Deadline Tracker",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Add multiple tasks with different deadlines
        Instant deadline1 = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        Instant deadline2 = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(2).toInstant();

        AddToDoListTaskRequestDTO task1 = new AddToDoListTaskRequestDTO("Task 1", deadline1);
        AddToDoListTaskRequestDTO task2 = new AddToDoListTaskRequestDTO("Task 2", deadline2);

        mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task2)))
                .andExpect(status().isCreated());

        // Get aggregated deadlines
        mockMvc.perform(get("/api/todolists/" + todoListId + "/deadlines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Deadlines retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("PUT /api/todolists/{todoListId}/tasks/{taskId}/complete - Task Not Found (404)")
    public void testMarkTaskCompleteNotFound() throws Exception {
        // Create a todo list
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Test List",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        UUID nonExistentTaskId = UUID.randomUUID();

        // Try to mark non-existent task as complete
        mockMvc.perform(put("/api/todolists/" + todoListId + "/tasks/" + nonExistentTaskId + "/complete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_TASK_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("PUT /api/todolists/{todoListId}/tasks/{taskId} - Update Task Success (200 OK)")
    public void testUpdateTaskSuccess() throws Exception {
        // Create a todo list and add a task
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Update Test List",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Instant originalDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        AddToDoListTaskRequestDTO taskRequest = new AddToDoListTaskRequestDTO(
                "Original description",
                originalDeadline
        );

        MvcResult taskResult = mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String taskId = objectMapper.readTree(taskResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Update the task
        Instant updatedDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(2).toInstant();
        UpdateToDoListTaskRequestDTO updateRequest = new UpdateToDoListTaskRequestDTO(
                "Updated description with more details",
                updatedDeadline
        );

        mockMvc.perform(put("/api/todolists/" + todoListId + "/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("Updated description with more details"))
                .andExpect(jsonPath("$.data.id").value(taskId))
                .andExpect(jsonPath("$.message").value("Task updated successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));

        // Verify the task was actually updated
        mockMvc.perform(get("/api/todolists/" + todoListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tasks[0].description").value("Updated description with more details"));
    }

    @Test
    @DisplayName("PUT /api/todolists/{todoListId}/tasks/{taskId} - Update Task Not Found (404)")
    public void testUpdateTaskNotFound() throws Exception {
        // Create a todo list
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Test List",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        UUID nonExistentTaskId = UUID.randomUUID();
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        UpdateToDoListTaskRequestDTO updateRequest = new UpdateToDoListTaskRequestDTO(
                "Updated description",
                deadline
        );

        // Try to update non-existent task
        mockMvc.perform(put("/api/todolists/" + todoListId + "/tasks/" + nonExistentTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_TASK_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("PUT /api/todolists/{todoListId}/tasks/{taskId} - Update Task Validation Error (400)")
    public void testUpdateTaskValidationError() throws Exception {
        // Create a todo list and add a task
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Validation Test List",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Instant originalDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        AddToDoListTaskRequestDTO taskRequest = new AddToDoListTaskRequestDTO(
                "Original task",
                originalDeadline
        );

        MvcResult taskResult = mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String taskId = objectMapper.readTree(taskResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Try to update with empty request (both fields null)
        UpdateToDoListTaskRequestDTO updateRequest = new UpdateToDoListTaskRequestDTO(null, null);

        mockMvc.perform(put("/api/todolists/" + todoListId + "/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TODO_VAL_002"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("DELETE /api/todolists/{todoListId}/tasks/{taskId} - Delete Task Success (200 OK)")
    public void testDeleteTaskSuccess() throws Exception {
        // Create a todo list and add a task
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Delete Test List",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        AddToDoListTaskRequestDTO taskRequest = new AddToDoListTaskRequestDTO(
                "Task to delete",
                deadline
        );

        MvcResult taskResult = mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String taskId = objectMapper.readTree(taskResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Delete the task
        mockMvc.perform(delete("/api/todolists/" + todoListId + "/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deletion successful"))
                .andExpect(jsonPath("$.statusCode").value(200));

        // Verify the task was actually deleted
        mockMvc.perform(get("/api/todolists/" + todoListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tasks").isEmpty());
    }

    @Test
    @DisplayName("DELETE /api/todolists/{todoListId}/tasks/{taskId} - Delete Task Not Found (404)")
    public void testDeleteTaskNotFound() throws Exception {
        // Create a todo list
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Test List",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        UUID nonExistentTaskId = UUID.randomUUID();

        // Try to delete non-existent task
        mockMvc.perform(delete("/api/todolists/" + todoListId + "/tasks/" + nonExistentTaskId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_TASK_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("DELETE /api/todolists/{todoListId}/tasks/{taskId} - Delete Task Twice (404)")
    public void testDeleteTaskTwice() throws Exception {
        // Create a todo list and add a task
        CreateToDoListRequestDTO createRequest = new CreateToDoListRequestDTO(
                "Double Delete Test",
                testUserId
        );

        MvcResult createResult = mockMvc.perform(post("/api/todolists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String todoListId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        AddToDoListTaskRequestDTO taskRequest = new AddToDoListTaskRequestDTO(
                "Task to delete twice",
                deadline
        );

        MvcResult taskResult = mockMvc.perform(post("/api/todolists/" + todoListId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String taskId = objectMapper.readTree(taskResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Delete the task once
        mockMvc.perform(delete("/api/todolists/" + todoListId + "/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        // Try to delete again - should fail
        mockMvc.perform(delete("/api/todolists/" + todoListId + "/tasks/" + taskId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_TASK_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }
}

package com.cpp.project.uc_8_add_todo_list_task;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-8.5.03: Accepts description at 500-char limit
 */
public class UC_8_5_03_AddTask_Success_MaxLength_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.03: Accept task with 500 character description (max limit)")
    public void testAddTaskSuccessMaxLength() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8503@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create a future deadline (tomorrow)
        Instant futureDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        // Create a description with exactly 500 characters
        String maxDescription = "A".repeat(5000);

        // Act
        ToDoListTaskDTO result = toDoListService.addTaskToList(
                todoList.getId(),
                maxDescription,
                futureDeadline
        );

        // Assert
        assertNotNull(result);
        assertEquals(5000, result.getDescription().length());
        assertEquals(maxDescription, result.getDescription());
    }
}

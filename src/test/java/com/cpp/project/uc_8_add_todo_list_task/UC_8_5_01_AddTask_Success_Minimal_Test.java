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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-8.5.01: Short non-empty description; deadline slightly in future
 */
public class UC_8_5_01_AddTask_Success_Minimal_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.01: Add task with minimal description and deadline 1 minute in future")
    public void testAddTaskSuccessMinimal() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8501@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create deadline 1 minute in future (boundary case)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        Date futureDeadline = calendar.getTime();

        // Act
        ToDoListTaskDTO result = toDoListService.addTaskToList(
                todoList.getId(),
                "Buy milk",
                futureDeadline
        );

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Buy milk", result.getDescription());
        assertEquals(futureDeadline, result.getDeadline());
    }
}

package com.cpp.project.uc_13_update_todolist_task;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.entity.ToDoListErrorCode;
import com.cpp.project.todolist.entity.ToDoListException;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-13.02: Fails to update task when task is not found
 */
public class UC_13_02_UpdateToDoListTask_Fail_TaskNotFound_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-13.02: Fails to update task with non-existent task ID")
    public void testUpdateNonExistentTask() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetodolisttask.uc1302@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Personal Tasks", userId);

        // Use a random UUID that doesn't exist
        UUID nonExistentTaskId = UUID.randomUUID();
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.updateTask(
                    todoList.getId(),
                    nonExistentTaskId,
                    "Updated description",
                    deadline
            );
        });

        assertEquals(ToDoListErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(nonExistentTaskId.toString()));
    }

    @Test
    @DisplayName("UC-13.02: Fails to update task with non-existent list ID")
    public void testUpdateTaskWithNonExistentList() {
        // Arrange
        UUID nonExistentListId = UUID.randomUUID();
        UUID nonExistentTaskId = UUID.randomUUID();
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.updateTask(
                    nonExistentListId,
                    nonExistentTaskId,
                    "Updated description",
                    deadline
            );
        });

        assertEquals(ToDoListErrorCode.TODO_LIST_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(nonExistentListId.toString()));
    }
}

package com.cpp.project.uc_14_delete_todolist_task;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
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
 * UC-14.02: Fails to delete task when task is not found
 */
public class UC_14_02_DeleteToDoListTask_Fail_TaskNotFound_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-14.02: Fails to delete task with non-existent task ID")
    public void testDeleteNonExistentTask() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetodolisttask.uc1402@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Personal Tasks", userId);

        // Use a random UUID that doesn't exist
        UUID nonExistentTaskId = UUID.randomUUID();

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.deleteTask(todoList.getId(), nonExistentTaskId);
        });

        assertEquals(ToDoListErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(nonExistentTaskId.toString()));
    }

    @Test
    @DisplayName("UC-14.02: Fails to delete task with non-existent list ID")
    public void testDeleteTaskWithNonExistentList() {
        // Arrange
        UUID nonExistentListId = UUID.randomUUID();
        UUID nonExistentTaskId = UUID.randomUUID();

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.deleteTask(nonExistentListId, nonExistentTaskId);
        });

        assertEquals(ToDoListErrorCode.TODO_LIST_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(nonExistentListId.toString()));
    }

    @Test
    @DisplayName("UC-14.02: Fails to delete the same task twice")
    public void testDeleteTaskTwice() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetodolisttask.uc1402b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Work Tasks", userId);

        // Add a task
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Task to delete",
                deadline
        );

        // Delete the task once
        toDoListService.deleteTask(todoList.getId(), task.getId());

        // Act & Assert - Try to delete again
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.deleteTask(todoList.getId(), task.getId());
        });

        assertEquals(ToDoListErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
    }
}

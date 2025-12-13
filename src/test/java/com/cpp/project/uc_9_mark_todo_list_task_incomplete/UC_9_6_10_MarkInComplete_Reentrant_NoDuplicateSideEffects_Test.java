package com.cpp.project.uc_9_mark_todo_list_task_incomplete;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.common.entity.TaskStatus;
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
 * UC-9.6.1: Multiple reset calls to markComplete leave a consistent COMPLETE state
 */
public class UC_9_6_10_MarkInComplete_Reentrant_NoDuplicateSideEffects_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-9.6.1: Multiple calls to markComplete are reentrant and consistent")
    public void testMarkInCompleteReentrantNoDuplicateSideEffects() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc961@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create a future deadline
        Instant futureDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();

        // Add a task
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Buy groceries",
                futureDeadline
        );

        // Act - Call markComplete multiple times rapidly
        ToDoListTaskDTO result1 = toDoListService.markTaskComplete(todoList.getId(), task.getId());
        ToDoListTaskDTO result2 = toDoListService.markTaskComplete(todoList.getId(), task.getId());

        // Assert - Both results should be consistent (COMPLETED)
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(TaskStatus.COMPLETED, result1.getStatus());
        assertEquals(TaskStatus.COMPLETED, result2.getStatus());
        assertEquals(result1.getDescription(), result2.getDescription());
        assertEquals(result1.getDeadline(), result2.getDeadline());

        // Act - Call markInComplete multiple times rapidly
        result1 = toDoListService.markTaskInComplete(todoList.getId(), task.getId());
        result2 = toDoListService.markTaskInComplete(todoList.getId(), task.getId());

        // Assert - Both results should be consistent (COMPLETED)
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(TaskStatus.PENDING, result1.getStatus());
        assertEquals(TaskStatus.PENDING, result2.getStatus());
        assertEquals(result1.getDescription(), result2.getDescription());
        assertEquals(result1.getDeadline(), result2.getDeadline());
    }
}

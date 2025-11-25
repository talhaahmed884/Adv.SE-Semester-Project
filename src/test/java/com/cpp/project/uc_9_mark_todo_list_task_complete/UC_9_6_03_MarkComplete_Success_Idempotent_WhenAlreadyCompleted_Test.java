package com.cpp.project.uc_9_mark_todo_list_task_complete;

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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-9.6.03: If already COMPLETED, markComplete is a no-op (remains COMPLETED)
 */
public class UC_9_6_03_MarkComplete_Success_Idempotent_WhenAlreadyCompleted_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-9.6.03: Mark complete when already COMPLETED is idempotent")
    public void testMarkCompleteIdempotentWhenAlreadyCompleted() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc9603@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Buy groceries",
                futureDeadline
        );

        // Mark task as complete first time
        ToDoListTaskDTO firstComplete = toDoListService.markTaskComplete(todoList.getId(), task.getId());
        assertEquals(TaskStatus.COMPLETED, firstComplete.getStatus());

        // Act - Mark task as complete again (already completed)
        ToDoListTaskDTO secondComplete = toDoListService.markTaskComplete(todoList.getId(), task.getId());

        // Assert - Should remain completed (idempotent)
        assertNotNull(secondComplete);
        assertEquals(TaskStatus.COMPLETED, secondComplete.getStatus());
    }
}

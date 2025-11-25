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
 * UC-9.6.06: Completing ahead of the deadline is allowed
 */
public class UC_9_6_06_MarkComplete_Allowed_WhenDeadlineInFuture_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-9.6.06: Allow marking complete when deadline is in future")
    public void testMarkCompleteAllowedWhenDeadlineInFuture() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc9606@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create a deadline 1 hour in the future
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        Date futureDeadline = calendar.getTime();

        // Add a task
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Buy groceries",
                futureDeadline
        );

        // Act - Mark task as complete (before deadline)
        ToDoListTaskDTO result = toDoListService.markTaskComplete(todoList.getId(), task.getId());

        // Assert - Should complete successfully
        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
    }
}

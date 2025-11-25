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
 * UC-9.6.05: markComplete must not mutate the deadline
 */
public class UC_9_6_05_MarkComplete_DoesNotAlterDeadline_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-9.6.05: Mark complete does not alter deadline")
    public void testMarkCompleteDoesNotAlterDeadline() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc9605@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create a specific future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date originalDeadline = calendar.getTime();

        // Add a task
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Buy groceries",
                originalDeadline
        );

        // Act - Mark task as complete
        ToDoListTaskDTO result = toDoListService.markTaskComplete(todoList.getId(), task.getId());

        // Assert - Deadline should remain unchanged
        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        assertEquals(originalDeadline, result.getDeadline());
    }
}

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
 * UC-9.6.12: If description were null due to upstream bug, markComplete still completes without NPE
 */
public class UC_9_6_12_MarkComplete_Robust_DespiteNullOptionalFields_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-9.6.12: Mark complete handles null optional fields robustly")
    public void testMarkCompleteRobustDespiteNullOptionalFields() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc9612@test.com",
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

        // Act - Mark task as complete (should handle null fields gracefully)
        ToDoListTaskDTO result = toDoListService.markTaskComplete(todoList.getId(), task.getId());

        // Assert - Should complete without NPE
        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
    }
}

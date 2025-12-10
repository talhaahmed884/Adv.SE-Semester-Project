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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-8.5.09: Null deadline is allowed (deadline is optional for todo list tasks)
 */
public class UC_8_5_09_AddTask_Success_NullDeadline_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.09: Allow null deadline (deadline is optional)")
    public void testAddTaskSuccessNullDeadline() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8509@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Act - Add task with null deadline
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Task without deadline",
                null // Deadline is optional
        );

        // Assert - Task should be created successfully
        assertNotNull(task);
        assertNotNull(task.getId());
        assertEquals("Task without deadline", task.getDescription());
        assertNull(task.getDeadline(), "Deadline should be null");
        assertNotNull(task.getStatus());
    }
}

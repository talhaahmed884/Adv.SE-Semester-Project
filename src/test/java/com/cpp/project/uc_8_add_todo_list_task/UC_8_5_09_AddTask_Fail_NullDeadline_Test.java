package com.cpp.project.uc_8_add_todo_list_task;

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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-8.5.09: Null deadline rejected
 */
public class UC_8_5_09_AddTask_Fail_NullDeadline_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.09: Reject null deadline")
    public void testAddTaskFailNullDeadline() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8509@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.addTaskToList(todoList.getId(), "Valid description", null);
        });

        assertEquals(ToDoListErrorCode.INVALID_TASK_DEADLINE.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("deadline") ||
                exception.getMessage().toLowerCase().contains("null"));
    }
}

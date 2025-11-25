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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


/**
 * UC-8.5.1: Deadline 1ms in the past rejected
 */
public class UC_8_5_10_AddTask_Fail_PastDeadline_Tiny_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.1: Reject deadline 1ms in the past")
    public void testAddTaskFailPastDeadlineTiny() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc851@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create deadline 1ms in the past
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -1);
        Date pastDeadline = calendar.getTime();

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.addTaskToList(todoList.getId(), "Late task", pastDeadline);
        });

        assertEquals(ToDoListErrorCode.INVALID_TASK_DEADLINE.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("deadline") ||
                exception.getMessage().toLowerCase().contains("past"));
    }
}

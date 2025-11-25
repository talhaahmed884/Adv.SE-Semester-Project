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

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-8.5.05: Allows deadline equal to 'now' (=> now)
 */
public class UC_8_5_05_AddTask_Success_DeadlineEqualsNow_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.05: Allow deadline equal to now (boundary)")
    public void testAddTaskSuccessDeadlineEqualsNow() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8505@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create deadline equal to now (boundary case)
        Date now = new Date();

        // Act
        ToDoListTaskDTO result = toDoListService.addTaskToList(
                todoList.getId(),
                "Urgent task",
                now
        );

        // Assert
        assertNotNull(result);
        assertEquals("Urgent task", result.getDescription());
        assertNotNull(result.getDeadline());
    }
}

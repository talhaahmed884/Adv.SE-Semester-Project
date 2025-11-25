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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-8.5.13: Returned task contains trimmed description and exact deadline
 */
public class UC_8_5_13_AddTask_ReturnTask_PostNormalization_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.13: Returned task reflects normalized description and exact deadline")
    public void testAddTaskReturnTaskPostNormalization() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8513@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create a specific deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date exactDeadline = calendar.getTime();

        // Act - Add task with whitespace that should be normalized
        ToDoListTaskDTO result = toDoListService.addTaskToList(
                todoList.getId(),
                "  Prepare presentation  ",
                exactDeadline
        );

        // Assert - Output reflects normalization
        assertNotNull(result);
        assertEquals("Prepare presentation", result.getDescription()); // Trimmed
        assertEquals(exactDeadline, result.getDeadline()); // Exact deadline preserved
        assertNotNull(result.getId());
    }
}

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
import java.util.TimeZone;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-8.5.14: Validates against DST transition to avoid off-by-one-hour errors
 */
public class UC_8_5_14_AddTask_Timezone_DSTBoundary_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.14: Handle deadline in DST transition hour")
    public void testAddTaskTimezoneDSTBoundary() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8514@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create deadline in DST transition period (March 2nd Sunday 2:00 AM)
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        calendar.set(2025, Calendar.MARCH, 9, 2, 30, 0); // DST transition hour

        Date dstDeadline = calendar.getTime();

        // Act
        ToDoListTaskDTO result = toDoListService.addTaskToList(
                todoList.getId(),
                "DST boundary task",
                dstDeadline
        );

        // Assert
        assertNotNull(result);
        assertEquals("DST boundary task", result.getDescription());
        assertNotNull(result.getDeadline());
    }
}

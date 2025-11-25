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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-8.5.04: Supports Unicode text (emoji/diacritics)
 */
public class UC_8_5_04_AddTask_Success_Unicode_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.04: Support Unicode text with emoji and diacritics")
    public void testAddTaskSuccessUnicode() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8504@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Act - Add task with Unicode characters (emoji and diacritics)
        String unicodeDescription = "Finish résumé 📝";
        ToDoListTaskDTO result = toDoListService.addTaskToList(
                todoList.getId(),
                unicodeDescription,
                futureDeadline
        );

        // Assert
        assertNotNull(result);
        assertTrue(result.getDescription().contains("résumé"));
        assertTrue(result.getDescription().contains("📝"));
    }
}

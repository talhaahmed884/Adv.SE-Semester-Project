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

import java.text.Normalizer;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-8.5.17: Normalizes Unicode to NFC to avoid visually identical but not equal
 */
public class UC_8_5_17_AddTask_I18N_NormalizeNFC_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.17: Normalize Unicode to NFC form")
    public void testAddTaskI18NNormalizeNFC() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8517@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Create decomposed Unicode string (NFD form)
        String decomposed = "re\u0301sume\u0301"; // résumé in decomposed form (NFD)
        String composed = Normalizer.normalize(decomposed, Normalizer.Form.NFC); // résumé in composed form (NFC)

        // Act - Add task with decomposed Unicode
        ToDoListTaskDTO result = toDoListService.addTaskToList(
                todoList.getId(),
                decomposed,
                futureDeadline
        );

        // Assert - Should be normalized to NFC
        assertNotNull(result);
        // The description should be normalized (either to composed or consistently handled)
        assertTrue(result.getDescription().contains("é") ||
                result.getDescription().equals(composed));
    }
}

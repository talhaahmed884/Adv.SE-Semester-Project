package com.cpp.project.uc_8_add_todo_list_task;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-8.5.15: Add 1000 tasks without O(n^2) behavior
 */
public class UC_8_5_15_AddTask_Performance_Batch_1000_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-8.5.15: Add 1000 tasks without quadratic slowdown")
    public void testAddTaskPerformanceBatch1000() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.task.uc8515@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Performance Test Tasks", userId);

        // Create a future deadline
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDeadline = calendar.getTime();

        // Act - Add 1000 tasks and measure time
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            toDoListService.addTaskToList(
                    todoList.getId(),
                    "Task " + i,
                    futureDeadline
            );
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert - Should complete without O(n^2) slowdown
        System.out.println("Added 1000 tasks in " + duration + "ms");
        assertTrue(duration < 10000, "Adding 1000 tasks took " + duration + "ms, expected < 10000ms");
    }
}

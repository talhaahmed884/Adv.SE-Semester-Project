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
import java.util.TimeZone;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-9.6.08: Completing around DST transitions does not shift status improperly
 */
public class UC_9_6_08_MarkComplete_Timezone_DSTBoundary_NoChange_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-9.6.08: Mark complete around DST transition works correctly")
    public void testMarkCompleteTimezoneDSTBoundaryNoChange() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.complete.uc9608@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("My Tasks", userId);

        // Create deadline in DST transition period
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        calendar.set(2025, Calendar.MARCH, 9, 2, 30, 0);
        Date dstDeadline = calendar.getTime();

        // Add a task
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "DST task",
                dstDeadline
        );

        // Act - Mark task as complete
        ToDoListTaskDTO result = toDoListService.markTaskComplete(todoList.getId(), task.getId());

        // Assert - Should complete successfully without issues
        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
    }
}

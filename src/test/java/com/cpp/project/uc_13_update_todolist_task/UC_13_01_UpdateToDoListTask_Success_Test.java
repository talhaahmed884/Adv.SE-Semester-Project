package com.cpp.project.uc_13_update_todolist_task;

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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-13.01: Updates a todo list task with valid description and deadline
 */
public class UC_13_01_UpdateToDoListTask_Success_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-13.01: Updates task with all fields changed")
    public void testUpdateToDoListTaskAllFields() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetodolisttask.uc1301@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Personal Tasks", userId);

        // Create a task
        Instant originalDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Original task description",
                originalDeadline
        );

        // Prepare updated values
        String updatedDescription = "Updated task description with more details";
        Instant updatedDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(2).toInstant();

        // Act
        ToDoListTaskDTO result = toDoListService.updateTask(
                todoList.getId(),
                task.getId(),
                updatedDescription,
                updatedDeadline
        );

        // Assert
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(updatedDescription, result.getDescription());
        assertEquals(updatedDeadline, result.getDeadline());
        assertEquals(todoList.getId(), result.getTodoListId());
    }

    @Test
    @DisplayName("UC-13.01: Updates only task description")
    public void testUpdateToDoListTaskDescriptionOnly() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetodolisttask.uc1301b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Work Tasks", userId);

        // Create a task
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Original description",
                deadline
        );

        // Act - Update only the description
        String updatedDescription = "New task description";
        ToDoListTaskDTO result = toDoListService.updateTask(
                todoList.getId(),
                task.getId(),
                updatedDescription,
                deadline
        );

        // Assert
        assertNotNull(result);
        assertEquals(updatedDescription, result.getDescription());
        assertEquals(deadline, result.getDeadline());
    }

    @Test
    @DisplayName("UC-13.01: Updates only task deadline")
    public void testUpdateToDoListTaskDeadlineOnly() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetodolisttask.uc1301c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Study Tasks", userId);

        // Create a task
        Instant originalDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        String description = "Prepare for exam";
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                description,
                originalDeadline
        );

        // Act - Update only the deadline
        Instant newDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(7).toInstant();
        ToDoListTaskDTO result = toDoListService.updateTask(
                todoList.getId(),
                task.getId(),
                description,
                newDeadline
        );

        // Assert
        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(newDeadline, result.getDeadline());
    }

    @Test
    @DisplayName("UC-13.01: Updates task with null deadline (optional)")
    public void testUpdateToDoListTaskDeadlineToNull() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetodolisttask.uc1301d@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Flexible Tasks", userId);

        // Create a task with deadline
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Task with deadline",
                deadline
        );

        // Act - Update with null deadline
        ToDoListTaskDTO result = toDoListService.updateTask(
                todoList.getId(),
                task.getId(),
                "Task with deadline",
                null
        );

        // Assert
        assertNotNull(result);
        assertNull(result.getDeadline());
    }

    @Test
    @DisplayName("UC-13.01: Updates task from null deadline to specific deadline")
    public void testUpdateToDoListTaskFromNullToSpecificDeadline() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.updatetodolisttask.uc1301e@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Scheduled Tasks", userId);

        // Create a task without deadline
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Task without deadline",
                null
        );

        // Verify initial state
        assertNull(task.getDeadline());

        // Act - Update with specific deadline
        Instant newDeadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(3).toInstant();
        ToDoListTaskDTO result = toDoListService.updateTask(
                todoList.getId(),
                task.getId(),
                "Task without deadline",
                newDeadline
        );

        // Assert
        assertNotNull(result);
        assertEquals(newDeadline, result.getDeadline());
    }
}

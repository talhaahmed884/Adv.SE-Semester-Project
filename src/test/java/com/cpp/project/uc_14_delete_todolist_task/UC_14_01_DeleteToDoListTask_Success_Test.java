package com.cpp.project.uc_14_delete_todolist_task;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.dto.ToDoListTaskDTO;
import com.cpp.project.todolist.entity.ToDoListErrorCode;
import com.cpp.project.todolist.entity.ToDoListException;
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
 * UC-14.01: Deletes a todo list task successfully
 */
public class UC_14_01_DeleteToDoListTask_Success_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-14.01: Deletes a single task from a todo list")
    public void testDeleteToDoListTaskSuccess() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetodolisttask.uc1401@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Personal Tasks", userId);

        // Add a task
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Task to delete",
                deadline
        );

        // Verify task exists before deletion
        ToDoListDTO listBefore = toDoListService.getToDoListById(todoList.getId());
        assertEquals(1, listBefore.getTasks().size());

        // Act
        toDoListService.deleteTask(todoList.getId(), task.getId());

        // Assert - Verify task is deleted
        ToDoListDTO listAfter = toDoListService.getToDoListById(todoList.getId());
        assertEquals(0, listAfter.getTasks().size());
    }

    @Test
    @DisplayName("UC-14.01: Deletes one task while keeping others in the list")
    public void testDeleteOneTaskKeepOthers() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetodolisttask.uc1401b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Work Tasks", userId);

        // Add multiple tasks
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        ToDoListTaskDTO task1 = toDoListService.addTaskToList(
                todoList.getId(),
                "Task 1 - Keep",
                deadline
        );
        ToDoListTaskDTO task2 = toDoListService.addTaskToList(
                todoList.getId(),
                "Task 2 - Delete",
                deadline
        );
        ToDoListTaskDTO task3 = toDoListService.addTaskToList(
                todoList.getId(),
                "Task 3 - Keep",
                deadline
        );

        // Verify all tasks exist before deletion
        ToDoListDTO listBefore = toDoListService.getToDoListById(todoList.getId());
        assertEquals(3, listBefore.getTasks().size());

        // Act - Delete task2
        toDoListService.deleteTask(todoList.getId(), task2.getId());

        // Assert - Verify only task2 is deleted
        ToDoListDTO listAfter = toDoListService.getToDoListById(todoList.getId());
        assertEquals(2, listAfter.getTasks().size());

        // Verify the correct tasks remain
        assertTrue(listAfter.getTasks().stream()
                .anyMatch(t -> t.getId().equals(task1.getId())));
        assertTrue(listAfter.getTasks().stream()
                .anyMatch(t -> t.getId().equals(task3.getId())));
        assertFalse(listAfter.getTasks().stream()
                .anyMatch(t -> t.getId().equals(task2.getId())));
    }

    @Test
    @DisplayName("UC-14.01: Deletes a task without deadline")
    public void testDeleteTaskWithoutDeadline() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetodolisttask.uc1401c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Flexible Tasks", userId);

        // Add a task without deadline
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Task without deadline",
                null
        );

        // Act - Delete task
        toDoListService.deleteTask(todoList.getId(), task.getId());

        // Assert - Verify task is deleted
        ToDoListDTO listAfter = toDoListService.getToDoListById(todoList.getId());
        assertEquals(0, listAfter.getTasks().size());
    }

    @Test
    @DisplayName("UC-14.01: Deletes a completed task")
    public void testDeleteCompletedTask() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetodolisttask.uc1401d@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Completed Tasks", userId);

        // Add a task
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Task to complete and delete",
                deadline
        );

        // Mark task as complete
        toDoListService.markTaskComplete(todoList.getId(), task.getId());

        // Act - Delete completed task
        toDoListService.deleteTask(todoList.getId(), task.getId());

        // Assert - Verify task is deleted
        ToDoListDTO listAfter = toDoListService.getToDoListById(todoList.getId());
        assertEquals(0, listAfter.getTasks().size());
    }

    @Test
    @DisplayName("UC-14.01: Attempting to access deleted task throws exception")
    public void testDeletedTaskCannotBeAccessed() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.deletetodolisttask.uc1401e@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Create a todo list
        ToDoListDTO todoList = toDoListService.createToDoList("Test List", userId);

        // Add a task
        Instant deadline = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).toInstant();
        ToDoListTaskDTO task = toDoListService.addTaskToList(
                todoList.getId(),
                "Task to delete",
                deadline
        );

        // Act - Delete task
        toDoListService.deleteTask(todoList.getId(), task.getId());

        // Assert - Attempting to complete the deleted task should fail
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.markTaskComplete(todoList.getId(), task.getId());
        });

        assertEquals(ToDoListErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
    }
}

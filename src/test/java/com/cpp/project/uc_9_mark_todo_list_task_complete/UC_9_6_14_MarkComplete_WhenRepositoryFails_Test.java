package com.cpp.project.uc_9_mark_todo_list_task_complete;

import com.cpp.project.todolist.entity.ToDoList;
import com.cpp.project.todolist.entity.ToDoListErrorCode;
import com.cpp.project.todolist.entity.ToDoListException;
import com.cpp.project.todolist.repository.ToDoListRepository;
import com.cpp.project.todolist.service.ToDoListServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UC-9.6.14: Attempts to mark to-do list task complete when repository ran into runtime failure
 * This is a unit test using Mockito to simulate repository failure
 */
@ExtendWith(MockitoExtension.class)
public class UC_9_6_14_MarkComplete_WhenRepositoryFails_Test {
    @Mock
    private ToDoListRepository toDoListRepository;

    @InjectMocks
    private ToDoListServiceImpl toDoListService;

    @Test
    @DisplayName("UC-9.6.14: Throws exception when repository fails to save mark complete")
    public void testMarkCompleteWhenRepositoryFails() {
        // Arrange
        UUID todoListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        // Mock todo list retrieval - return a mock list with a task
        ToDoList mockToDoList = ToDoList.builder()
                .name("My Tasks")
                .userId(UUID.randomUUID())
                .build();

        // Add a mock task to the list
        mockToDoList.addTask("Buy groceries", new Date(System.currentTimeMillis() + 86400000));
        mockToDoList.getTasks().getFirst().setId(taskId);

        when(toDoListRepository.findById(todoListId)).thenReturn(Optional.of(mockToDoList));

        // Mock repository save to throw exception
        when(toDoListRepository.save(any(ToDoList.class)))
                .thenThrow(new jakarta.persistence.PersistenceException("Database connection failed"));

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.markTaskComplete(todoListId, taskId);
        });

        assertEquals(ToDoListErrorCode.TASK_UPDATE_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));

        // Verify that repository save was attempted
        verify(toDoListRepository, times(1)).save(any(ToDoList.class));
    }

    @Test
    @DisplayName("UC-9.6.14: Throws exception when todo list not found")
    public void testMarkCompleteWhenToDoListNotFound() {
        // Arrange
        UUID todoListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        // Mock repository to return empty (list not found)
        when(toDoListRepository.findById(todoListId)).thenReturn(Optional.empty());

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.markTaskComplete(todoListId, taskId);
        });

        assertEquals(ToDoListErrorCode.TODO_LIST_NOT_FOUND.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("not found"));

        // Verify that repository findById was called
        verify(toDoListRepository, times(1)).findById(todoListId);
    }

    @Test
    @DisplayName("UC-9.6.14: Throws exception when task not found in list")
    public void testMarkCompleteWhenTaskNotFound() {
        // Arrange
        UUID todoListId = UUID.randomUUID();
        UUID nonExistentTaskId = UUID.randomUUID();

        // Mock todo list retrieval - return a mock list with a different task
        ToDoList mockToDoList = ToDoList.builder()
                .name("My Tasks")
                .userId(UUID.randomUUID())
                .build();

        // Add a task with a different ID
        mockToDoList.addTask("Buy groceries", new Date(System.currentTimeMillis() + 86400000));
        mockToDoList.getTasks().getFirst().setId(UUID.randomUUID());

        when(toDoListRepository.findById(todoListId)).thenReturn(Optional.of(mockToDoList));

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.markTaskComplete(todoListId, nonExistentTaskId);
        });

        assertEquals(ToDoListErrorCode.TASK_NOT_FOUND.getCode(), exception.getCode());

        // Verify that repository findById was called but save was not
        verify(toDoListRepository, times(1)).findById(todoListId);
        verify(toDoListRepository, never()).save(any(ToDoList.class));
    }
}

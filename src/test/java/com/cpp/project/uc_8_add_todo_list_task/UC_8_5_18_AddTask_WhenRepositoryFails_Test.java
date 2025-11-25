package com.cpp.project.uc_8_add_todo_list_task;

import com.cpp.project.common.sanitization.adapter.ToDoListTaskSanitizer;
import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.service.ToDoListValidationService;
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
 * UC-8.5.18: Attempts to add to-do list task when repository ran into runtime failure
 * This is a unit test using Mockito to simulate repository failure
 */
@ExtendWith(MockitoExtension.class)
public class UC_8_5_18_AddTask_WhenRepositoryFails_Test {
    @Mock
    private ToDoListRepository toDoListRepository;

    @Mock
    private ToDoListValidationService validationService;

    @Mock
    private ToDoListTaskSanitizer sanitizer;

    @InjectMocks
    private ToDoListServiceImpl toDoListService;

    @Test
    @DisplayName("UC-8.5.18: Throws exception when repository fails to save task")
    public void testAddTaskWhenRepositoryFails() {
        // Arrange
        UUID todoListId = UUID.randomUUID();
        String description = "Buy groceries";
        Date deadline = new Date(System.currentTimeMillis() + 86400000); // tomorrow

        // Mock todo list retrieval - return a mock list
        ToDoList mockToDoList = ToDoList.builder()
                .name("My Tasks")
                .userId(UUID.randomUUID())
                .build();

        when(sanitizer.sanitizeDescription(description)).thenReturn(description);
        when(validationService.validateTaskDescription(description)).thenReturn(new ValidationResultBuilder().build());
        when(validationService.validateTaskDeadline(deadline)).thenReturn(new ValidationResultBuilder().build());

        when(toDoListRepository.findById(todoListId)).thenReturn(Optional.of(mockToDoList));

        // Mock repository save to throw exception
        when(toDoListRepository.save(any(ToDoList.class)))
                .thenThrow(new jakarta.persistence.PersistenceException("Database connection failed"));

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.addTaskToList(todoListId, description, deadline);
        });

        assertEquals(ToDoListErrorCode.TASK_CREATION_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));

        // Verify that repository save was attempted
        verify(toDoListRepository, times(1)).save(any(ToDoList.class));
    }

    @Test
    @DisplayName("UC-8.5.18: Throws exception when todo list not found")
    public void testAddTaskWhenToDoListNotFound() {
        // Arrange
        UUID todoListId = UUID.randomUUID();
        String description = "Buy groceries";
        Date deadline = new Date(System.currentTimeMillis() + 86400000); // tomorrow

        when(sanitizer.sanitizeDescription(description)).thenReturn(description);
        when(validationService.validateTaskDescription(description)).thenReturn(new ValidationResultBuilder().build());
        when(validationService.validateTaskDeadline(deadline)).thenReturn(new ValidationResultBuilder().build());

        // Mock repository to return empty (list not found)
        when(toDoListRepository.findById(todoListId)).thenReturn(Optional.empty());

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.addTaskToList(todoListId, description, deadline);
        });

        assertEquals(ToDoListErrorCode.TODO_LIST_NOT_FOUND.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("not found"));

        // Verify that repository findById was called
        verify(toDoListRepository, times(1)).findById(todoListId);
    }
}

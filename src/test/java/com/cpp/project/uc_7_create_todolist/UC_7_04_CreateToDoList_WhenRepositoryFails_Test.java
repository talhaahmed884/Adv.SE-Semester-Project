package com.cpp.project.uc_7_create_todolist;

import com.cpp.project.common.sanitization.adapter.ToDoListSanitizer;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UC-7.04: Attempts to create to-do list when repository ran into failure
 * This is a unit test using Mockito to simulate repository failure
 */
@ExtendWith(MockitoExtension.class)
public class UC_7_04_CreateToDoList_WhenRepositoryFails_Test {
    @Mock
    private ToDoListRepository toDoListRepository;

    @Mock
    private ToDoListValidationService validationService;

    @Mock
    private ToDoListSanitizer listSanitizer;

    @InjectMocks
    private ToDoListServiceImpl toDoListService;

    @Test
    @DisplayName("UC-7.04: Throws exception when repository fails during save")
    public void testCreateToDoListWhenRepositoryFails() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String name = "Shopping List";

        // Mock sanitization
        when(listSanitizer.sanitizeName(name)).thenReturn("Shopping List");

        // Mock validation - return valid result
        when(validationService.validateName(any())).thenReturn(
                new ValidationResultBuilder().build()
        );

        // Mock repository - save throws exception
        when(toDoListRepository.save(any(ToDoList.class)))
                .thenThrow(new jakarta.persistence.PersistenceException("Database connection failed"));

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.createToDoList(name, userId);
        });

        assertEquals(ToDoListErrorCode.TODO_LIST_CREATION_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));

        // Verify that repository save was attempted
        verify(toDoListRepository, times(1)).save(any(ToDoList.class));
    }

    @Test
    @DisplayName("UC-7.04: Throws exception when sanitizer fails")
    public void testCreateToDoListWhenSanitizerFails() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String name = "Shopping List";

        // Mock sanitization - throws exception
        when(listSanitizer.sanitizeName(name))
                .thenThrow(new RuntimeException("Sanitization error"));

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.createToDoList(name, userId);
        });

        assertEquals(ToDoListErrorCode.TODO_LIST_CREATION_FAILED.getCode(), exception.getCode());
    }
}

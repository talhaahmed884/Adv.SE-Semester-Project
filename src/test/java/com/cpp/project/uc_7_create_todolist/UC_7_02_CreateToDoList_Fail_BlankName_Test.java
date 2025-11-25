package com.cpp.project.uc_7_create_todolist;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.entity.ToDoListErrorCode;
import com.cpp.project.todolist.entity.ToDoListException;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-7.02: Null or blank name rejected
 */
public class UC_7_02_CreateToDoList_Fail_BlankName_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-7.02: Rejects null to-do list name")
    public void testCreateToDoListNullName() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc702a@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.createToDoList(null, userId);
        });

        assertEquals(ToDoListErrorCode.INVALID_TODO_LIST_NAME.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("name"));
    }

    @Test
    @DisplayName("UC-7.02: Rejects empty to-do list name")
    public void testCreateToDoListEmptyName() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc702b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.createToDoList("", userId);
        });

        assertEquals(ToDoListErrorCode.INVALID_TODO_LIST_NAME.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("UC-7.02: Rejects whitespace-only to-do list name")
    public void testCreateToDoListWhitespaceName() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc702c@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        // Act & Assert
        ToDoListException exception = assertThrows(ToDoListException.class, () -> {
            toDoListService.createToDoList("   ", userId);
        });

        assertEquals(ToDoListErrorCode.INVALID_TODO_LIST_NAME.getCode(), exception.getCode());
    }
}

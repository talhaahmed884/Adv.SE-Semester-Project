package com.cpp.project.uc_7_create_todolist;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UC-7.01: Creates a to-do list with a non-empty name
 */
public class UC_7_01_CreateToDoList_Success_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-7.01: Creates a to-do list with a non-empty name")
    public void testCreateToDoListSuccess() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc701@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        String name = "Shopping List";

        // Act
        ToDoListDTO result = toDoListService.createToDoList(name, userId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Shopping List", result.getName());
        assertEquals(userId, result.getUserId());
    }
}

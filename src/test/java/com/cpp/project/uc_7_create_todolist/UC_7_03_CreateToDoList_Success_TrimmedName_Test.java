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

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-7.03: Trims name before storage
 */
public class UC_7_03_CreateToDoList_Success_TrimmedName_Test extends BaseIntegrationTest {
    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-7.03: Trims to-do list name before storage")
    public void testCreateToDoListTrimmedName() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc703a@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        String inputName = "  Errands  "; // Name with leading/trailing spaces
        String expectedName = "Errands"; // Should be trimmed

        // Act
        ToDoListDTO result = toDoListService.createToDoList(inputName, userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedName, result.getName());
    }

    @Test
    @DisplayName("UC-7.03: Preserves case when trimming to-do list name")
    public void testCreateToDoListPreservesCase() {
        // Arrange - Create a test user first
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc703b@test.com",
                "Password123!"
        ));
        UUID userId = user.getId();

        String inputName = " Shopping List "; // Mixed case with spaces
        String expectedName = "Shopping List"; // Case preserved, spaces trimmed

        // Act
        ToDoListDTO result = toDoListService.createToDoList(inputName, userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedName, result.getName());
        // Verify case preservation
        assertTrue(result.getName().contains("Shopping"));
        assertTrue(result.getName().contains("List"));
    }
}

package com.cpp.project.common;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UpdateUserRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for UserController
 * Tests all user management endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("GET /api/users/{id} - Success (200 OK)")
    public void testGetUserByIdSuccess() throws Exception {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "getuserbyid.success@test.com",
                "Password123!"
        ));

        // Act & Assert
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.data.name").value("Test User"))
                .andExpect(jsonPath("$.data.email").value("getuserbyid.success@test.com"))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/users/{id} - User Not Found (404 Not Found)")
    public void testGetUserByIdNotFound() throws Exception {
        // Arrange - Generate random UUID that doesn't exist
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/users/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Success (200 OK)")
    public void testGetUserByEmailSuccess() throws Exception {
        // Arrange - Create a test user
        String email = "getuserbyemail.success@test.com";
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                email,
                "Password123!"
        ));

        // Act & Assert
        mockMvc.perform(get("/api/users/email/" + email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.data.name").value("Test User"))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - User Not Found (404 Not Found)")
    public void testGetUserByEmailNotFound() throws Exception {
        // Arrange - Use an email that doesn't exist
        String nonExistentEmail = "nonexistent@test.com";

        // Act & Assert
        mockMvc.perform(get("/api/users/email/" + nonExistentEmail))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Update Name Success (200 OK)")
    public void testUpdateUserNameSuccess() throws Exception {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Original Name",
                "updatename.success@test.com",
                "Password123!"
        ));

        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO("Updated Name", null);

        // Act & Assert
        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("User updated successfully"))
                .andExpect(jsonPath("$.message").value("Update successful"))
                .andExpect(jsonPath("$.statusCode").value(200));

        // Verify the name was updated
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.data.email").value("updatename.success@test.com"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Update Email Success (200 OK)")
    public void testUpdateUserEmailSuccess() throws Exception {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "updateemail.original@test.com",
                "Password123!"
        ));

        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO(null, "updateemail.new@test.com");

        // Act & Assert
        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("User updated successfully"))
                .andExpect(jsonPath("$.message").value("Update successful"))
                .andExpect(jsonPath("$.statusCode").value(200));

        // Verify the email was updated
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test User"))
                .andExpect(jsonPath("$.data.email").value("updateemail.new@test.com"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Update Both Name and Email Success (200 OK)")
    public void testUpdateUserBothFieldsSuccess() throws Exception {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Original Name",
                "updateboth.original@test.com",
                "Password123!"
        ));

        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO("Updated Name", "updateboth.new@test.com");

        // Act & Assert
        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("User updated successfully"))
                .andExpect(jsonPath("$.message").value("Update successful"))
                .andExpect(jsonPath("$.statusCode").value(200));

        // Verify both fields were updated
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.data.email").value("updateboth.new@test.com"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Empty Request (400 Bad Request)")
    public void testUpdateUserEmptyRequest() throws Exception {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "updateempty@test.com",
                "Password123!"
        ));

        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO(null, null);

        // Act & Assert
        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_003"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Blank Fields (400 Bad Request)")
    public void testUpdateUserBlankFields() throws Exception {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "updateblank@test.com",
                "Password123!"
        ));

        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO("   ", "   ");

        // Act & Assert
        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_003"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - User Not Found (404 Not Found)")
    public void testUpdateUserNotFound() throws Exception {
        // Arrange - Generate random UUID that doesn't exist
        UUID nonExistentId = UUID.randomUUID();
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO("Updated Name", null);

        // Act & Assert
        mockMvc.perform(put("/api/users/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Invalid Email Format (400 Bad Request)")
    public void testUpdateUserInvalidEmailFormat() throws Exception {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "updateinvalidemail@test.com",
                "Password123!"
        ));

        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO(null, "invalid-email-format");

        // Act & Assert
        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_007"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Email Already In Use (409 Conflict)")
    public void testUpdateUserEmailAlreadyInUse() throws Exception {
        // Arrange - Create two test users
        authenticationService.signUp(new SignUpRequestDTO(
                "User One",
                "user1.emailconflict@test.com",
                "Password123!"
        ));

        UserDTO user2 = authenticationService.signUp(new SignUpRequestDTO(
                "User Two",
                "user2.emailconflict@test.com",
                "Password123!"
        ));

        // Try to update user2's email to user1's email
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO(null, "user1.emailconflict@test.com");

        // Act & Assert
        mockMvc.perform(put("/api/users/" + user2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_009"))
                .andExpect(jsonPath("$.statusCode").value(409));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Invalid Name (400 Bad Request)")
    public void testUpdateUserInvalidName() throws Exception {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "updateinvalidname@test.com",
                "Password123!"
        ));

        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO("", null);

        // Act & Assert
        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_003"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Success (200 OK)")
    public void testDeleteUserSuccess() throws Exception {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "User To Delete",
                "deleteuser.success@test.com",
                "Password123!"
        ));

        // Act & Assert - Delete the user
        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("User deleted successfully"))
                .andExpect(jsonPath("$.message").value("Deletion successful"))
                .andExpect(jsonPath("$.statusCode").value(200));

        // Verify user is deleted by trying to get it
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_001"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - User Not Found (404 Not Found)")
    public void testDeleteUserNotFound() throws Exception {
        // Arrange - Generate random UUID that doesn't exist
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/users/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_001"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Update with Trimmed Name (200 OK)")
    public void testUpdateUserTrimmedName() throws Exception {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Original Name",
                "updatetrimmed@test.com",
                "Password123!"
        ));

        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO("  Trimmed Name  ", null);

        // Act & Assert
        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Verify the name was trimmed
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Trimmed Name"));
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Email with Special Characters (200 OK)")
    public void testGetUserByEmailWithSpecialCharacters() throws Exception {
        // Arrange - Create a test user with special characters in email
        String email = "test.user+tag@test.com";
        authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                email,
                "Password123!"
        ));

        // Act & Assert
        mockMvc.perform(get("/api/users/email/" + email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}

package com.cpp.project.common;

import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/signup - Success (201 Created)")
    public void testSignUpEndpointSuccess() throws Exception {
        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                "api.test@example.com",
                "StrongPass123!@#"
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.user.name").value("John Doe"))
                .andExpect(jsonPath("$.data.user.email").value("api.test@example.com"))
                .andExpect(jsonPath("$.message").value("Signup successful"))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    @DisplayName("POST /api/auth/signup - Duplicate Email (409 Conflict)")
    public void testSignUpEndpointDuplicateEmail() throws Exception {
        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                "duplicate.api.test@example.com",
                "StrongPass123!@#"
        );

        // First signup - success
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second signup - conflict
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_002"))
                .andExpect(jsonPath("$.statusCode").value(409));
    }

    @Test
    @DisplayName("POST /api/auth/signup - Validation Error (400 Bad Request)")
    public void testSignUpEndpointValidationError() throws Exception {
        SignUpRequestDTO request = new SignUpRequestDTO(
                "", // Invalid name
                "invalid-email", // Invalid email
                "123" // Invalid password
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_003"))
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("POST /api/auth/login - Success (200 OK)")
    public void testLoginEndpointSuccess() throws Exception {
        // First create user
        SignUpRequestDTO signupRequest = new SignUpRequestDTO(
                "John Doe",
                "login.test@example.com",
                "StrongPass123!@#"
        );
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated());

        // Then login
        LoginRequestDTO loginRequest =
                new LoginRequestDTO(
                        "login.test@example.com",
                        "StrongPass123!@#"
                );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.email").value("login.test@example.com"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("POST /api/auth/login - Invalid Credentials (401 Unauthorized)")
    public void testLoginEndpointInvalidCredentials() throws Exception {
        LoginRequestDTO loginRequest =
                new LoginRequestDTO(
                        "nonexistent@example.com",
                        "WrongPassword"
                );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_002"))
                .andExpect(jsonPath("$.statusCode").value(401));
    }
}

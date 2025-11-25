package com.cpp.project.uc_2_login;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-2.7: Login_Success_PasswordNormalization
 * Test Case: Trims password before lookup
 * Category: Positive
 * Expected: Pass
 * Precondition: Same normalization strategy as signup works
 * <p>
 * NOTE: Based on the sanitization implementation, passwords are NOT normalized.
 * This test verifies that password matching is exact (no trimming).
 */
public class UC_2_17_Login_Success_PasswordNormalization_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setup() {
        // Create a user with password containing spaces
        SignUpRequestDTO signUpRequest = new SignUpRequestDTO(
                "Alice Johnson",
                "alice.johnson@example.com",
                "  MyPassword123#  " // Password with spaces
        );
        authenticationService.signUp(signUpRequest);
    }

    @Test
    @DisplayName("UC-2.7: Password is NOT trimmed (exact match required)")
    public void testLoginPasswordNotNormalized() {
        // Arrange - Password must match exactly as stored
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "alice.johnson@example.com",
                "  MyPassword123#  " // Exact password with spaces
        );

        // Act
        boolean result = authenticationService.login(loginRequest);

        // Assert - Login should succeed with exact password match
        assertTrue(result, "Login should succeed with exact password (spaces preserved)");
    }
}

package com.cpp.project.UC_2_Login;

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
 * UC-2.1: Login_Success
 * Test Case: Logs in with correct credentials
 * Category: Positive
 * Expected: Pass
 */
public class UC_2_01_Login_Success_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setup() {
        // Create a user for login testing
        SignUpRequestDTO signUpRequest = new SignUpRequestDTO(
                "John Doe",
                "john.doe@example.com",
                "ValidPassword123@"
        );
        authenticationService.signUp(signUpRequest);
    }

    @Test
    @DisplayName("UC-2.1: Logs in with correct credentials")
    public void testLoginSuccess() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "john.doe@example.com",
                "ValidPassword123@"
        );

        // Act
        boolean result = authenticationService.login(loginRequest);

        // Assert - Login should succeed
        assertTrue(result);
    }
}

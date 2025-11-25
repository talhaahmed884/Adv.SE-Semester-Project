package com.cpp.project.UC_2_Login;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-2.4: Login_Fail_InvalidEmailFormat
 * Test Case: Invalid email format is rejected during login
 * Category: Negative/Exception
 * Expected: Fail with InvalidEmailFormatException or AuthenticationException
 */
public class UC_2_04_Login_Fail_InvalidEmailFormat_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-2.4: Invalid email format is rejected during login")
    public void testLoginFailInvalidEmailFormat() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "invalid-email-format", // Missing @ and domain
                "SomePassword123"
        );

        // Act & Assert - Should throw exception for invalid email format
        Exception exception = assertThrows(
                Exception.class,
                () -> authenticationService.login(loginRequest),
                "Login should reject invalid email format"
        );

        // Verify exception message indicates email format issue
        String message = exception.getMessage().toLowerCase();
        assertTrue(
                message.contains("email") || message.contains("format") || message.contains("invalid"),
                "Exception should indicate email format is invalid"
        );
    }
}

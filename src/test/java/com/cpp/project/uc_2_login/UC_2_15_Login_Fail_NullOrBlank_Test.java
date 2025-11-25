package com.cpp.project.uc_2_login;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.entity.AuthenticationErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-2.5: Login_Fail_NullOrBlank
 * Test Case: Null or blank email/password
 * Category: Negative/Exception
 * Expected: Fail with NullArgumentException
 */
public class UC_2_15_Login_Fail_NullOrBlank_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-2.5: Null email should be rejected")
    public void testLoginFailNullEmail() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                null, // Null email
                "SomePassword123"
        );

        // Act & Assert - Should throw exception
        Exception exception = assertThrows(
                Exception.class,
                () -> authenticationService.login(loginRequest),
                "Login should reject null email"
        );

        // Verify exception message
        // Verify exception message
        assertEquals(AuthenticationErrorCode.INVALID_CREDENTIALS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("UC-2.5: Null password should be rejected")
    public void testLoginFailNullPassword() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "user@example.com",
                null // Null password
        );

        // Act & Assert - Should throw exception
        Exception exception = assertThrows(
                Exception.class,
                () -> authenticationService.login(loginRequest),
                "Login should reject null password"
        );

        // Verify exception message
        assertEquals(AuthenticationErrorCode.INVALID_CREDENTIALS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("UC-2.5: Blank email should be rejected")
    public void testLoginFailBlankEmail() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "  ", // Blank email (only whitespace)
                "SomePassword123"
        );

        // Act & Assert - Should throw exception
        Exception exception = assertThrows(
                Exception.class,
                () -> authenticationService.login(loginRequest),
                "Login should reject blank email"
        );

        // Verify exception message
        String message = exception.getMessage().toLowerCase();
        assertTrue(
                message.contains("blank") || message.contains("empty") || message.contains("invalid"),
                "Exception should indicate argument is blank or invalid"
        );
    }

    @Test
    @DisplayName("UC-2.5: Blank password should be rejected")
    public void testLoginFailBlankPassword() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "user@example.com",
                "  " // Blank password (only whitespace)
        );

        // Act & Assert - Should throw exception
        Exception exception = assertThrows(
                Exception.class,
                () -> authenticationService.login(loginRequest),
                "Login should reject blank password"
        );

        // Verify exception message
        String message = exception.getMessage().toLowerCase();
        assertTrue(
                message.contains("blank") || message.contains("empty") || message.contains("invalid"),
                "Exception should indicate argument is blank or invalid"
        );
    }
}

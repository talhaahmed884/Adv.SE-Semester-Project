package com.cpp.project.UC_2_Login;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.entity.AuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-2.3: Login_Fail_NoSuchUser
 * Test Case: Email not registered
 * Category: Negative/Exception
 * Expected: Fail with AuthenticationException
 */
public class UC_2_03_Login_Fail_NoSuchUser_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-2.3: Email not registered")
    public void testLoginFailNoSuchUser() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "nonexistent.user@example.com", // User doesn't exist
                "SomePassword123"
        );

        // Act & Assert - Should throw AuthenticationException
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authenticationService.login(loginRequest),
                "Login should throw AuthenticationException for non-existent user"
        );

        // Verify exception message contains "Invalid" (don't reveal user existence)
        assertTrue(
                exception.getMessage().toLowerCase().contains("invalid") ||
                        exception.getMessage().toLowerCase().contains("credentials"),
                "Exception should indicate invalid credentials without revealing user existence"
        );
    }
}

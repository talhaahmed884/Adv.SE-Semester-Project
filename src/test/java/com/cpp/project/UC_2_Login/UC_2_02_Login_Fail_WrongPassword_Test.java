package com.cpp.project.UC_2_Login;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * UC-2.2: Login_Fail_WrongPassword
 * Test Case: Rejects wrong password
 * Category: Negative/Exception
 * Expected: Fail with AuthenticationException
 */
public class UC_2_02_Login_Fail_WrongPassword_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setup() {
        // Create a user for login testing
        SignUpRequestDTO signUpRequest = new SignUpRequestDTO(
                "Jane Smith",
                "jane.smith@example.com",
                "CorrectPassword123@"
        );
        authenticationService.signUp(signUpRequest);
    }

    @Test
    @DisplayName("UC-2.2: Rejects wrong password")
    public void testLoginFailWrongPassword() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "jane.smith@example.com",
                "WrongPassword456@" // Incorrect password
        );

        // Act
        boolean result = authenticationService.login(loginRequest);

        // Assert - Login should fail
        assertFalse(result, "Login should fail with wrong password");
    }
}

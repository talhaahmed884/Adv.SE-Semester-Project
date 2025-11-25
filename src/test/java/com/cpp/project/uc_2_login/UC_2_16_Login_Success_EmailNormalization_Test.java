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
 * UC-2.6: Login_Success_EmailNormalization
 * Test Case: Trims and lowercases email before lookup
 * Category: Positive
 * Expected: Pass
 * Precondition: Same normalization strategy as signup works
 */
public class UC_2_16_Login_Success_EmailNormalization_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setup() {
        // Create a user with normalized email
        SignUpRequestDTO signUpRequest = new SignUpRequestDTO(
                "Bob Wilson",
                "bob.wilson@example.com", // Stored as lowercase
                "ValidPassword123@"
        );
        authenticationService.signUp(signUpRequest);
    }

    @Test
    @DisplayName("UC-2.6: Trims and lowercases email before lookup")
    public void testLoginSuccessEmailNormalization() {
        // Arrange - Email with spaces and mixed case
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "  Bob.Wilson@Example.COM  ", // Spaces + mixed case
                "ValidPassword123@"
        );

        // Act
        boolean result = authenticationService.login(loginRequest);

        // Assert - Login should succeed after email normalization
        assertTrue(result, "Login should succeed with email normalization (trim + lowercase)");
    }
}

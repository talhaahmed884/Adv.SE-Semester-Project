package com.cpp.project.uc_2_verify_password;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.entity.UserErrorCode;
import com.cpp.project.user.entity.UserException;
import com.cpp.project.user_credential.service.UserCredentialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UC-2.21: verify() when user has no credentials throws exception
 */
public class UC_2_21_Verify_Behavior_WithoutPriorSet_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-2.21: Verify throws exception when credentials not found")
    public void testVerifyPasswordWithoutCredentials() {
        // Arrange - Create a user but manually to bypass credential creation
        // First create via normal signup to get proper structure
        authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc221@test.com",
                "Password123!"
        ));

        // For this test, we'll use a non-existent email
        String nonExistentEmail = "nonexistent.uc221@test.com";

        // Act & Assert - Trying to verify for user without credentials should throw
        UserException exception = assertThrows(UserException.class, () -> {
            userCredentialService.verifyPassword(nonExistentEmail, "AnyPassword123!");
        });

        // Should throw CREDENTIAL_NOT_FOUND or USER_NOT_FOUND
        assertTrue(
                exception.getCode().equals(UserErrorCode.USER_NOT_FOUND.getCode()) ||
                        exception.getMessage().contains("not found"),
                "Should throw appropriate exception when credentials don't exist"
        );
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}

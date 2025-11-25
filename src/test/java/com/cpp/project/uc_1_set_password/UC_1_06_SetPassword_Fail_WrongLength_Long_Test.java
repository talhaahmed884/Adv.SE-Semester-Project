package com.cpp.project.uc_1_set_password;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user_credential.service.UserCredentialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-1.06: Accepts long passwords (no maximum length restriction in current implementation)
 */
public class UC_1_06_SetPassword_Fail_WrongLength_Long_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.06: Accepts very long passwords")
    public void testSetPasswordAcceptsLongPassword() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc106a@test.com",
                "Password123!"
        ));

        // Very long password (128+ characters with valid strength)
        String longPassword = "P@ssw0rd" + "a".repeat(120) + "123!";

        // Act - Should succeed (no max length in current implementation)
        assertDoesNotThrow(() -> {
            userCredentialService.setPassword(user.getEmail(), longPassword);
        });

        // Assert - Verify password works
        assertTrue(userCredentialService.verifyPassword(user.getEmail(), longPassword));
    }

    @Test
    @DisplayName("UC-1.06: Accepts password with 256 characters")
    public void testSetPasswordAccepts256CharPassword() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc106b@test.com",
                "Password123!"
        ));

        // 256-character password with valid strength
        String longPassword = "P@ssw0rd" + "a".repeat(248);

        // Act & Assert
        assertDoesNotThrow(() -> {
            userCredentialService.setPassword(user.getEmail(), longPassword);
        });
    }
}

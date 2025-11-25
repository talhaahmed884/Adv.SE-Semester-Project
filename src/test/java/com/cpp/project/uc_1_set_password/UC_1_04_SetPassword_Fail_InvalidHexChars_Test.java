package com.cpp.project.uc_1_set_password;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user_credential.entity.UserCredentialErrorCode;
import com.cpp.project.user_credential.entity.UserCredentialException;
import com.cpp.project.user_credential.service.UserCredentialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UC-1.04: Rejects weak passwords (password strength validation)
 */
public class UC_1_04_SetPassword_Fail_InvalidHexChars_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.04: Rejects password without uppercase letters")
    public void testSetPasswordFailNoUppercase() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc104a@test.com",
                "Password123!"
        ));

        // Act & Assert - password without uppercase
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.setPassword(user.getEmail(), "password123!");
        });

        assertEquals(UserCredentialErrorCode.PASSWORD_REQUIRED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("UC-1.04: Rejects password without digits")
    public void testSetPasswordFailNoDigits() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc104b@test.com",
                "Password123!"
        ));

        // Act & Assert - password without digits
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.setPassword(user.getEmail(), "PasswordOnly!");
        });

        assertEquals(UserCredentialErrorCode.PASSWORD_REQUIRED.getCode(), exception.getCode());
    }
}

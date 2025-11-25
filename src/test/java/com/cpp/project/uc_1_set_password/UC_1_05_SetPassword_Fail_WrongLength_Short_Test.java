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
 * UC-1.05: Rejects password shorter than minimum length
 */
public class UC_1_05_SetPassword_Fail_WrongLength_Short_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.05: Rejects password with 7 characters (too short)")
    public void testSetPasswordFailTooShort() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc105a@test.com",
                "Password123!"
        ));

        // Act & Assert - Password too short (< 8 characters)
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.setPassword(user.getEmail(), "Pass1!");
        });

        assertEquals(UserCredentialErrorCode.PASSWORD_REQUIRED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("UC-1.05: Rejects password with only 1 character")
    public void testSetPasswordFailSingleChar() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc105b@test.com",
                "Password123!"
        ));

        // Act & Assert
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.setPassword(user.getEmail(), "P");
        });

        assertEquals(UserCredentialErrorCode.PASSWORD_REQUIRED.getCode(), exception.getCode());
    }
}

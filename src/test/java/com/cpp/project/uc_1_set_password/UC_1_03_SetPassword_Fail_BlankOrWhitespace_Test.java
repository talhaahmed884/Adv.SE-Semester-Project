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
 * UC-1.03: Blank or whitespace-only password is rejected
 */
public class UC_1_03_SetPassword_Fail_BlankOrWhitespace_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.03: Rejects empty password")
    public void testSetPasswordFailEmpty() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc103a@test.com",
                "Password123!"
        ));

        // Act & Assert
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.setPassword(user.getEmail(), "");
        });

        assertEquals(UserCredentialErrorCode.PASSWORD_REQUIRED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("UC-1.03: Rejects whitespace-only password")
    public void testSetPasswordFailWhitespace() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc103b@test.com",
                "Password123!"
        ));

        // Act & Assert
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.setPassword(user.getEmail(), "   ");
        });

        assertEquals(UserCredentialErrorCode.PASSWORD_REQUIRED.getCode(), exception.getCode());
    }
}

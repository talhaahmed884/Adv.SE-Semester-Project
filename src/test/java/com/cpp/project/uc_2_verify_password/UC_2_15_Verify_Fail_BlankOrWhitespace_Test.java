package com.cpp.project.uc_2_verify_password;

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
 * UC-2.15: Blank or whitespace-only password is rejected
 */
public class UC_2_15_Verify_Fail_BlankOrWhitespace_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-2.15: Empty password throws exception")
    public void testVerifyPasswordFailEmpty() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc215a@test.com",
                "Password123!"
        ));

        // Act & Assert
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.verifyPassword(user.getEmail(), "");
        });

        assertEquals(UserCredentialErrorCode.PASSWORD_REQUIRED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("UC-2.15: Whitespace-only password throws exception")
    public void testVerifyPasswordFailWhitespace() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc215b@test.com",
                "Password123!"
        ));

        // Act & Assert
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.verifyPassword(user.getEmail(), "   ");
        });

        assertEquals(UserCredentialErrorCode.PASSWORD_REQUIRED.getCode(), exception.getCode());
    }
}

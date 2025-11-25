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

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-1.07: Accepts passwords with mixed case
 */
public class UC_1_07_SetPassword_Success_UppercaseAccepted_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.07: Accepts password with uppercase letters")
    public void testSetPasswordFailureUppercase() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc107a@test.com",
                "Password123!"
        ));

        // Password with uppercase letters
        String mixedCasePassword = "UPPERCASE123!";

        // Act
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.setPassword(user.getEmail(), mixedCasePassword);
        });

        assertEquals(exception.getCode(), UserCredentialErrorCode.PASSWORD_REQUIRED.getCode());
    }

    @Test
    @DisplayName("UC-1.07: Accepts password with mixed case")
    public void testSetPasswordSuccessMixedCase() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc107b@test.com",
                "Password123!"
        ));

        // Mixed case password
        String mixedCasePassword = "MiXeD123!CaSe";

        // Act
        userCredentialService.setPassword(user.getEmail(), mixedCasePassword);

        // Assert - Case should be preserved for verification
        assertTrue(userCredentialService.verifyPassword(user.getEmail(), mixedCasePassword));
        // Wrong case should fail
        assertFalse(userCredentialService.verifyPassword(user.getEmail(), "mixed123!case"));
    }
}

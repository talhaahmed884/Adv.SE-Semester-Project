package com.cpp.project.uc_2_verify_password;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user_credential.service.UserCredentialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * UC-2.13: Returns false for non-matching password
 */
public class UC_2_13_Verify_False_NonMatching_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-2.13: Returns false when password does not match")
    public void testVerifyPasswordFalseNonMatching() {
        // Arrange - Create a test user with known password
        String correctPassword = "SecurePassword123!";
        String wrongPassword = "WrongPassword456!";

        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc213@test.com",
                correctPassword
        ));

        // Act - Verify with wrong password
        boolean result = userCredentialService.verifyPassword(user.getEmail(), wrongPassword);

        // Assert
        assertFalse(result, "Password verification should fail with incorrect password");
    }

    @Test
    @DisplayName("UC-2.13: Returns false for password with one character different")
    public void testVerifyPasswordFalseSingleCharDifferent() {
        // Arrange
        String correctPassword = "SecurePassword123!";
        String almostCorrect = "SecurePassword124!"; // Last digit different

        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc213b@test.com",
                correctPassword
        ));

        // Act
        boolean result = userCredentialService.verifyPassword(user.getEmail(), almostCorrect);

        // Assert
        assertFalse(result, "Even one character difference should cause verification to fail");
    }
}

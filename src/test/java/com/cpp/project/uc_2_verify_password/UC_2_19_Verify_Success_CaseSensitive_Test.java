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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-2.19: Password verification is case-sensitive
 */
public class UC_2_19_Verify_Success_CaseSensitive_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-2.19: Exact case match succeeds")
    public void testVerifyPasswordCaseSensitiveExactMatch() {
        // Arrange
        String password = "MixedCasePassword123!";
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc219a@test.com",
                password
        ));

        // Act - Verify with exact case
        boolean result = userCredentialService.verifyPassword(user.getEmail(), password);

        // Assert
        assertTrue(result, "Exact case match should succeed");
    }

    @Test
    @DisplayName("UC-2.19: Wrong case fails verification")
    public void testVerifyPasswordCaseSensitiveFails() {
        // Arrange
        String password = "MixedCasePassword123!";
        String wrongCase = "mixedcasepassword123!"; // All lowercase

        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc219b@test.com",
                password
        ));

        // Act - Verify with wrong case
        boolean result = userCredentialService.verifyPassword(user.getEmail(), wrongCase);

        // Assert
        assertFalse(result, "Wrong case should fail verification");
    }
}

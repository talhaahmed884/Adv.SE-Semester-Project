package com.cpp.project.uc_2_verify_password;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user_credential.service.UserCredentialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-2.12: Returns true when provided password matches stored password
 */
public class UC_2_12_Verify_Success_Matching_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-2.12: Returns true when password matches")
    public void testVerifyPasswordSuccessMatching() {
        // Arrange - Create a test user with known password
        String password = "SecurePassword123!";
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc212@test.com",
                password
        ));

        // Act - Verify with the correct password
        boolean result = userCredentialService.verifyPassword(user.getEmail(), password);

        // Assert
        assertTrue(result, "Password verification should succeed with correct password");
    }
}

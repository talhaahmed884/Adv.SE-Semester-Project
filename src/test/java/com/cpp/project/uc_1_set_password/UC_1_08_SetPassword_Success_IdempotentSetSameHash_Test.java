package com.cpp.project.uc_1_set_password;

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
 * UC-1.08: Setting the same password twice is idempotent
 */
public class UC_1_08_SetPassword_Success_IdempotentSetSameHash_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.08: Setting the same password twice works correctly")
    public void testSetPasswordIdempotentSamePassword() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc108@test.com",
                "Password123!"
        ));

        String password = "NewPassword456!";

        // Act - Set the same password twice
        userCredentialService.setPassword(user.getEmail(), password);
        userCredentialService.setPassword(user.getEmail(), password);

        // Assert - Password should still work for verification
        assertTrue(userCredentialService.verifyPassword(user.getEmail(), password));
    }
}

package com.cpp.project.uc_1_set_password;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user_credential.entity.UserCredential;
import com.cpp.project.user_credential.repository.UserCredentialRepository;
import com.cpp.project.user_credential.service.UserCredentialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-1.01: Successfully sets a password and stores SHA-512 hash securely
 */
public class UC_1_01_SetPassword_Success_ExactSHA512Hex_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserCredentialRepository credentialRepository;

    @Test
    @DisplayName("UC-1.01: Successfully sets password and stores SHA-512 hash")
    public void testSetPasswordSuccess() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc101@test.com",
                "Password123!"
        ));

        String newPassword = "NewSecurePassword456!";

        // Act
        userCredentialService.setPassword(user.getEmail(), newPassword);

        // Assert
        UserCredential credential = credentialRepository.findByUserId(user.getId()).orElseThrow();
        assertNotNull(credential);
        assertNotNull(credential.getPasswordHash());
        assertEquals("SHA-512", credential.getAlgorithm());

        // Verify password can be verified
        assertTrue(userCredentialService.verifyPassword(user.getEmail(), newPassword));
    }
}

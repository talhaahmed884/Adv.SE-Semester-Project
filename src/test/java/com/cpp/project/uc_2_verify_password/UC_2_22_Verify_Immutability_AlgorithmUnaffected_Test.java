package com.cpp.project.uc_2_verify_password;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * UC-2.22: verify() must not mutate algorithm field
 */
public class UC_2_22_Verify_Immutability_AlgorithmUnaffected_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserCredentialRepository credentialRepository;

    @Test
    @DisplayName("UC-2.22: Algorithm remains unchanged after verify operations")
    public void testVerifyDoesNotMutateAlgorithm() {
        // Arrange
        String password = "TestPassword123!";
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc222@test.com",
                password
        ));

        // Get algorithm before verify
        UserCredential credentialBefore = credentialRepository.findByUserId(user.getId()).orElseThrow();
        String algorithmBefore = credentialBefore.getAlgorithm();

        // Act - Perform multiple verify operations
        userCredentialService.verifyPassword(user.getEmail(), password); // Correct
        userCredentialService.verifyPassword(user.getEmail(), "WrongPassword123!"); // Wrong
        userCredentialService.verifyPassword(user.getEmail(), password); // Correct again

        // Assert - Algorithm should remain unchanged
        UserCredential credentialAfter = credentialRepository.findByUserId(user.getId()).orElseThrow();
        String algorithmAfter = credentialAfter.getAlgorithm();

        assertEquals("SHA-512", algorithmBefore);
        assertEquals("SHA-512", algorithmAfter);
        assertEquals(algorithmBefore, algorithmAfter, "Algorithm should not change after verify operations");
    }
}

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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * UC-1.10: Algorithm field remains 'SHA-512' constant
 */
public class UC_1_10_SetPassword_Success_AlgorithmImmutable_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserCredentialRepository credentialRepository;

    @Test
    @DisplayName("UC-1.10: Algorithm field remains 'SHA-512' and is immutable")
    public void testSetPasswordAlgorithmImmutable() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc110@test.com",
                "Password123!"
        ));

        // First password
        String firstPassword = "FirstPassword123!";
        // Second password (different)
        String secondPassword = "SecondPassword456!";

        // Act - Set password twice with different passwords
        userCredentialService.setPassword(user.getEmail(), firstPassword);
        UserCredential credential1 = credentialRepository.findByUserId(user.getId()).orElseThrow();
        String algorithmAfterFirst = credential1.getAlgorithm();

        userCredentialService.setPassword(user.getEmail(), secondPassword);
        UserCredential credential2 = credentialRepository.findByUserId(user.getId()).orElseThrow();
        String algorithmAfterSecond = credential2.getAlgorithm();

        // Assert - Algorithm should always be SHA-512 and unchanged
        assertEquals("SHA-512", algorithmAfterFirst);
        assertEquals("SHA-512", algorithmAfterSecond);
        assertEquals(algorithmAfterFirst, algorithmAfterSecond);
    }
}

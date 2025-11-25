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
 * UC-2.23: Performance test - 10k verify calls
 */
public class UC_2_23_Verify_Performance_ManyChecks_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-2.23: Can handle many verification attempts efficiently")
    public void testVerifyPasswordPerformanceManyChecks() {
        // Arrange
        String password = "TestPassword123!";
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc223@test.com",
                password
        ));

        // Warmup JVM
        for (int i = 0; i < 100; i++) {
            userCredentialService.verifyPassword(user.getEmail(), password);
        }

        // Act - Measure time for 10k verify calls
        long startTime = System.currentTimeMillis();
        int iterations = 10000;

        for (int i = 0; i < iterations; i++) {
            userCredentialService.verifyPassword(user.getEmail(), password);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTimePerCall = (double) totalTime / iterations;

        // Assert - Should complete in reasonable time
        // SHA-512 hashing is intentionally slow for security, so we expect ~1-10ms per call
        assertTrue(avgTimePerCall < 100,
                "Average time per verify call should be reasonable: " + avgTimePerCall + "ms");

        System.out.println("Performance: " + iterations + " verify calls in " + totalTime +
                "ms (avg: " + String.format("%.2f", avgTimePerCall) + "ms per call)");
    }
}

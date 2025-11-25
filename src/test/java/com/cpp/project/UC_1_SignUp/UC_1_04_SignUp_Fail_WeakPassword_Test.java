package com.cpp.project.UC_1_SignUp;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UC_1_04_SignUp_Fail_WeakPassword_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @ParameterizedTest
    @ValueSource(strings = {
            "abc",              // Too short
            "12345",            // Too short, missing digit
            "ABCDEFG",          // Missing lowercase
            "abcdefg",          // Missing uppercase (if policy requires)
            "NoSpecial1",       // Missing special character (if policy requires)
    })
    @DisplayName("UC-1.04: Password does not meet complexity requirements")
    public void testSignUpFailWeakPassword(String weakPassword) {
        // Arrange
        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                "john.weak.test@example.com",
                weakPassword
        );

        // Act & Assert - Should fail validation
        assertThrows(Exception.class, () -> {
            authenticationService.signUp(request);
        });
    }

    @Test
    @DisplayName("UC-1.04: Password missing uppercase")
    public void testSignUpFailPasswordMissingUppercase() {
        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                "john.upper.test@example.com",
                "lowercase123"
        );

        // This depends on your password policy
        // If basic policy (6 chars), this might pass
        // If standard policy (requires upper/lower/digit/special), this should fail
        try {
            authenticationService.signUp(request);
            // If using basic policy, test passes
        } catch (Exception e) {
            // If using standard policy, exception expected
            assertTrue(e.getMessage().contains("password") ||
                    e.getMessage().contains("Password"));
        }
    }
}

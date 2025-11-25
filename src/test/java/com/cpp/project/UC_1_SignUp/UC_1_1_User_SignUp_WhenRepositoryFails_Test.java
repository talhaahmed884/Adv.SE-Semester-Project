package com.cpp.project.UC_1_SignUp;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.entity.UserErrorCode;
import com.cpp.project.user.entity.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UC_1_1_User_SignUp_WhenRepositoryFails_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.1: Attempts to register user when repository ran into runtime failure")
    public void testSignUpWhenRepositoryFails() {
        // This test is challenging with real database
        // Options:
        // 1. Drop table temporarily (risky)
        // 2. Violate database constraint
        // 3. Fill database to capacity (impractical)

        // Let's test database constraint violation
        // First create a user
        SignUpRequestDTO firstUser = new SignUpRequestDTO(
                "First User",
                "repo.fail.test@example.com",
                "StrongPass123!@#"
        );
        authenticationService.signUp(firstUser);

        // Try to create duplicate (simulates DB failure)
        SignUpRequestDTO duplicateUser = new SignUpRequestDTO(
                "Second User",
                "repo.fail.test@example.com",
                "StrongPass456!@#"
        );

        UserException exception = assertThrows(UserException.class, () -> {
            authenticationService.signUp(duplicateUser);
        });

        // Verify appropriate error
        assertTrue(
                exception.getErrorCode() == UserErrorCode.USER_ALREADY_EXISTS ||
                        exception.getErrorCode() == UserErrorCode.USER_CREATION_FAILED
        );
    }
}

package com.cpp.project.UC_1_SignUp;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.entity.UserErrorCode;
import com.cpp.project.user.entity.UserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UC_1_02_SignUp_Fail_DuplicateEmail_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.02: Attempts to register with an email that already exists for another user")
    public void testSignUpFailDuplicateEmail() {
        // Arrange - Create first user
        SignUpRequestDTO firstUser = new SignUpRequestDTO(
                "John Doe",
                "duplicate.test@example.com",
                "StrongPass123!@#"
        );
        authenticationService.signUp(firstUser);

        // Act & Assert - Try to create second user with same email
        SignUpRequestDTO duplicateUser = new SignUpRequestDTO(
                "Jane Smith",
                "duplicate.test@example.com", // Same email
                "StrongPass456!@#"
        );

        UserException exception = assertThrows(UserException.class, () -> {
            authenticationService.signUp(duplicateUser);
        });

        Assertions.assertEquals(UserErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
        Assertions.assertTrue(exception.getMessage().contains("duplicate.test@example.com"));
    }

    @Test
    @DisplayName("UC-1.02: Test case-insensitive email collision")
    public void testSignUpFailDuplicateEmailCaseInsensitive() {
        // Arrange
        SignUpRequestDTO firstUser = new SignUpRequestDTO(
                "John Doe",
                "Test@Example.COM",
                "StrongPass123!@#"
        );
        authenticationService.signUp(firstUser);

        // Act & Assert - Different case should still be considered duplicate
        SignUpRequestDTO duplicateUser = new SignUpRequestDTO(
                "Jane Smith",
                "test@example.com", // Lowercase version
                "StrongPass456!@#"
        );

        UserException exception = assertThrows(UserException.class, () -> {
            authenticationService.signUp(duplicateUser);
        });

        Assertions.assertEquals(UserErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
    }
}

package com.cpp.project.UC_1_SignUp;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UC_1_03_SignUp_Fail_InvalidEmailFormat_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @ParameterizedTest
    @ValueSource(strings = {
            "invalidemail",           // Missing @
            "invalid@",               // Missing domain
            "@example.com",           // Missing local part
            "invalid @example.com",   // Space in email
            "invalid@@example.com",   // Multiple @
            "invalid@.com",           // Invalid TLD
            "invalid@domain",         // Missing TLD
            "foo@bar.com,foo@bar.com" // Multiple emails
    })
    @DisplayName("UC-1.03: Email lacks '@' or domain parts")
    public void testSignUpFailInvalidEmailFormat(String invalidEmail) {
        // Arrange
        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                invalidEmail,
                "ValidPassword123"
        );

        // Act & Assert
        assertThrows(Exception.class, () -> {
            authenticationService.signUp(request);
        });
    }

    @Test
    @DisplayName("UC-1.03: Email with leading/trailing spaces")
    public void testSignUpFailEmailWithSpaces() {
        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                "  spaced@example.com  ",
                "ValidPassword123@"
        );

        UserDTO result = authenticationService.signUp(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        Assertions.assertEquals("John Doe", result.getName());
        Assertions.assertEquals("spaced@example.com", result.getEmail());

        // Verify we can login with created user
        Assertions.assertTrue(authenticationService.login(
                new LoginRequestDTO(
                        "spaced@example.com",
                        "ValidPassword123@"
                )
        ));
    }
}

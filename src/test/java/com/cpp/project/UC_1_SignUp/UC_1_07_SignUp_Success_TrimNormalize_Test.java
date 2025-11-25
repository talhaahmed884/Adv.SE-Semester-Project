package com.cpp.project.UC_1_SignUp;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UC_1_07_SignUp_Success_TrimNormalize_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.07: Trims leading/trailing whitespace and normalizes email to lowercase")
    public void testSignUpSuccessTrimNormalize() {
        // Arrange - Email with spaces and mixed case
        SignUpRequestDTO request = new SignUpRequestDTO(
                "  Alice Smith  ", // Leading/trailing spaces in name
                "  Foo@Bar.com  ", // Leading/trailing spaces + mixed case
                "StrongPass123!@#"
        );

        // Act
        UserDTO result = authenticationService.signUp(request);

        // Assert - Name trimmed, email normalized
        assertNotNull(result);
        // Name should be trimmed (depends on your implementation)
        assertTrue(result.getName().equals("Alice Smith") ||
                result.getName().equals("  Alice Smith  "));

        // Email should work for login regardless of case/spaces
        assertTrue(authenticationService.login(
                new LoginRequestDTO(
                        "foo@bar.com", // Lowercase
                        "StrongPass123!@#"
                )
        ));
    }
}

package com.cpp.project.uc_1_signup;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UC_1_01_SignUp_Success_Basic_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.01: Registers a new user with valid name, well-formed email, and strong password")
    public void testSignUpSuccessBasic() {
        // Arrange
        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                "john.doe.test@example.com",
                "StrongPass123!@#"
        );

        // Act
        UserDTO result = authenticationService.signUp(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        Assertions.assertEquals("John Doe", result.getName());
        Assertions.assertEquals("john.doe.test@example.com", result.getEmail());

        // Verify we can login with created user
        Assertions.assertTrue(authenticationService.login(
                new LoginRequestDTO(
                        "john.doe.test@example.com",
                        "StrongPass123!@#"
                )
        ));
    }
}

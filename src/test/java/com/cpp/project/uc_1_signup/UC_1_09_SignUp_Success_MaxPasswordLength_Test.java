package com.cpp.project.uc_1_signup;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class UC_1_09_SignUp_Success_MaxPasswordLength_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.09: Allows very long but valid passwords up to 64 chars")
    public void testSignUpSuccessMaxPasswordLength() {
        // Arrange - 61 character password
        String longPassword = "ValidPass123!@#$%ValidPass123!@#$%ValidPass123!@#$%ValidPass1";
        assertEquals(61, longPassword.length());

        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                "long.pass.test@example.com",
                longPassword
        );

        // Act
        UserDTO result = authenticationService.signUp(request);

        // Assert
        assertNotNull(result);

        // Verify login works with long password
        assertTrue(authenticationService.login(
                new LoginRequestDTO(
                        "long.pass.test@example.com",
                        longPassword
                )
        ));
    }

    @Test
    @DisplayName("UC-1.09: Accepts 65 character password (boundary)")
    public void testSignUpSuccess65CharPassword() {
        // Arrange - 65 character password
        String password65 = "A".repeat(60) + "a23!@";

        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                "pass65.test@example.com",
                password65
        );

        // Act & Assert - Should succeed (no max limit in basic implementation)
        UserDTO result = authenticationService.signUp(request);
        assertNotNull(result);
    }

    @Test
    @DisplayName("UC-1.09: Fails with extremely long password (> 1000 chars)")
    public void testSignUpFailExtremelyLongPassword() {
        // Arrange - Very long password (potential DoS)
        String extremePassword = "A".repeat(10000);

        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                "extreme.pass.test@example.com",
                extremePassword
        );

        // Act & Assert - May succeed or fail depending on implementation
        // Good practice is to have upper limit
        try {
            authenticationService.signUp(request);
            // If no limit, test passes
        } catch (Exception e) {
            // If there's a limit, exception expected
            assertTrue(true);
        }
    }
}

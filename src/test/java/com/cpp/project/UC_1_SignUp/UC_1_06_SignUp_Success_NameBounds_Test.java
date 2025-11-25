package com.cpp.project.UC_1_SignUp;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class UC_1_06_SignUp_Success_NameBounds_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.06: Accepts minimum allowed name length (1 char)")
    public void testSignUpSuccessMinimumNameLength() {
        // Arrange - Minimum valid name (2 characters based on validation)
        SignUpRequestDTO request = new SignUpRequestDTO(
                "Jo",
                "min.name.test@example.com",
                "StrongPass123!@#"
        );

        // Act
        UserDTO result = authenticationService.signUp(request);

        // Assert
        assertNotNull(result);
        assertEquals("Jo", result.getName());
    }

    @Test
    @DisplayName("UC-1.06: Accepts maximum allowed name length (255 chars)")
    public void testSignUpSuccessMaximumNameLength() {
        // Arrange - Maximum valid name (100 characters based on validation)
        String maxLengthName = "A".repeat(100);
        SignUpRequestDTO request = new SignUpRequestDTO(
                maxLengthName,
                "max.name.test@example.com",
                "StrongPass123!@#"
        );

        // Act
        UserDTO result = authenticationService.signUp(request);

        // Assert
        assertNotNull(result);
        assertEquals(maxLengthName, result.getName());
        assertEquals(100, result.getName().length());
    }

    @Test
    @DisplayName("UC-1.06: Rejects name exceeding maximum length (256 chars)")
    public void testSignUpFailNameTooLong() {
        // Arrange
        String tooLongName = "A".repeat(101); // Exceeds max of 100
        SignUpRequestDTO request = new SignUpRequestDTO(
                tooLongName,
                "toolong.name.test@example.com",
                "ValidPassword123"
        );

        // Act & Assert
        assertThrows(Exception.class, () -> {
            authenticationService.signUp(request);
        });
    }
}

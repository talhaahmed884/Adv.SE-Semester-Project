package com.cpp.project.uc_2_verify_password;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.service.UserValidationService;
import com.cpp.project.user.entity.User;
import com.cpp.project.user.repository.UserRepository;
import com.cpp.project.user_credential.entity.UserCredentialErrorCode;
import com.cpp.project.user_credential.entity.UserCredentialException;
import com.cpp.project.user_credential.repository.UserCredentialRepository;
import com.cpp.project.user_credential.service.UserCredentialServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * UC-2.24: Attempts to verify password when repository ran into failure
 */
@ExtendWith(MockitoExtension.class)
public class UC_2_24_VerifyPassword_WhenRepositoryFails_Test {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCredentialRepository credentialRepository;

    @Mock
    private UserValidationService validationService;

    @InjectMocks
    private UserCredentialServiceImpl userCredentialService;

    @Test
    @DisplayName("UC-2.24: Throws exception when repository fails during credential lookup")
    public void testVerifyPasswordWhenRepositoryFails() {
        // Arrange
        String email = "test@example.com";
        String password = "TestPassword123!";

        // Mock validation
        when(validationService.validateEmail(any())).thenReturn(new ValidationResultBuilder().build());
        when(validationService.validatePasswordInput(any())).thenReturn(new ValidationResultBuilder().build());

        // Mock user lookup
        User mockUser = org.mockito.Mockito.mock(User.class);
        when(mockUser.getId()).thenReturn(java.util.UUID.randomUUID());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Mock repository to throw exception
        when(credentialRepository.findByUserId(any()))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.verifyPassword(email, password);
        });

        assertEquals(UserCredentialErrorCode.PASSWORD_VERIFICATION_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));
    }

    @Test
    @DisplayName("UC-2.24: Throws exception when user not found")
    public void testVerifyPasswordWhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "TestPassword123!";

        // Mock validation
        when(validationService.validateEmail(any())).thenReturn(new ValidationResultBuilder().build());
        when(validationService.validatePasswordInput(any())).thenReturn(new ValidationResultBuilder().build());

        // Mock user lookup - return empty
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userCredentialService.verifyPassword(email, password);
        });
    }
}

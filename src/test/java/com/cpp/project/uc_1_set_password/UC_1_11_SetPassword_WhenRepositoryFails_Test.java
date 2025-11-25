package com.cpp.project.uc_1_set_password;

import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.service.UserValidationService;
import com.cpp.project.user.entity.User;
import com.cpp.project.user.repository.UserRepository;
import com.cpp.project.user_credential.entity.UserCredential;
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
import static org.mockito.Mockito.*;

/**
 * UC-1.11: Attempts to set password when repository ran into failure
 * This is a unit test using Mockito to simulate repository failure
 */
@ExtendWith(MockitoExtension.class)
public class UC_1_11_SetPassword_WhenRepositoryFails_Test {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCredentialRepository credentialRepository;

    @Mock
    private UserValidationService validationService;

    @InjectMocks
    private UserCredentialServiceImpl userCredentialService;

    @Test
    @DisplayName("UC-1.11: Throws exception when repository fails during save")
    public void testSetPasswordWhenRepositorySaveFails() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "NewPassword123!";

        // Mock validation
        when(validationService.validateEmail(any())).thenReturn(new ValidationResultBuilder().build());
        when(validationService.validatePasswordStrength(any())).thenReturn(new ValidationResultBuilder().build());

        // Mock user lookup
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(java.util.UUID.randomUUID());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Mock credential lookup
        UserCredential mockCredential = mock(UserCredential.class);
        when(credentialRepository.findByUserId(any())).thenReturn(Optional.of(mockCredential));

        // Mock credential.setPasswordHash to do nothing
        doNothing().when(mockCredential).setPasswordHash(any());

        // Mock repository save to throw exception
        when(credentialRepository.save(any(UserCredential.class)))
                .thenThrow(new jakarta.persistence.PersistenceException("Database connection failed"));

        // Act & Assert
        UserCredentialException exception = assertThrows(UserCredentialException.class, () -> {
            userCredentialService.setPassword(email, newPassword);
        });

        assertEquals(UserCredentialErrorCode.CREDENTIAL_UPDATE_FAILED.getCode(), exception.getCode());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));

        // Verify that repository save was attempted
        verify(credentialRepository, times(1)).save(any(UserCredential.class));
    }

    @Test
    @DisplayName("UC-1.11: Throws exception when user not found")
    public void testSetPasswordWhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String newPassword = "NewPassword123!";

        // Mock validation
        when(validationService.validateEmail(any())).thenReturn(new ValidationResultBuilder().build());
        when(validationService.validatePasswordStrength(any())).thenReturn(new ValidationResultBuilder().build());

        // Mock user lookup - return empty
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userCredentialService.setPassword(email, newPassword);
        });

        // Verify that credential save was never attempted
        verify(credentialRepository, never()).save(any(UserCredential.class));
    }
}

package com.cpp.project.uc_2_login;

import com.cpp.project.authentication.service.AuthenticationServiceImpl;
import com.cpp.project.common.sanitization.adapter.LoginRequestSanitizer;
import com.cpp.project.common.validation.entity.ValidationResultBuilder;
import com.cpp.project.common.validation.service.UserValidationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import com.cpp.project.user.entity.AuthenticationErrorCode;
import com.cpp.project.user.entity.AuthenticationException;
import com.cpp.project.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UC-2.8: Login_WhenRepositoryFails
 * Test Case: Attempts to login user when repository ran into failure
 * Category: Negative/Exception
 * Expected: Fail with ExternalServiceException
 */
public class UC_2_18_Login_WhenRepositoryFails_Test extends BaseIntegrationTest {
    @Mock
    LoginRequestSanitizer loginRequestSanitizer;

    @Mock
    UserService userService;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    @DisplayName("UC-2.8: Attempts to login when repository fails")
    public void testLoginWhenRepositoryFails() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "user@example.com",
                "SomePassword123@"
        );

        when(loginRequestSanitizer.sanitize(loginRequest)).thenReturn(loginRequest);
        when(userValidationService.validateLoginRequest(any(LoginRequestDTO.class))).thenReturn(
                new ValidationResultBuilder().build());

        when(userService.getUserByEmail(loginRequest.getEmail())).thenThrow(
                new jakarta.persistence.PersistenceException("Database connection failed"));

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(loginRequest);
        });

        assertEquals(AuthenticationErrorCode.AUTHENTICATION_FAILED.getCode(), exception.getCode());

        verify(userService, times(1)).getUserByEmail(any(String.class));
    }
}

package com.cpp.project.UC_1_SignUp;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UC_1_08_SignUp_Fail_NullArgs_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-1.08: Any argument is null - name")
    public void testSignUpFailNullName() {
        SignUpRequestDTO request = new SignUpRequestDTO(
                null,
                "valid.test@example.com",
                "ValidPassword123"
        );

        assertThrows(Exception.class, () -> {
            authenticationService.signUp(request);
        });
    }

    @Test
    @DisplayName("UC-1.08: Any argument is null - email")
    public void testSignUpFailNullEmail() {
        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                null,
                "ValidPassword123"
        );

        assertThrows(Exception.class, () -> {
            authenticationService.signUp(request);
        });
    }

    @Test
    @DisplayName("UC-1.08: Any argument is null - password")
    public void testSignUpFailNullPassword() {
        SignUpRequestDTO request = new SignUpRequestDTO(
                "John Doe",
                "valid.test@example.com",
                null
        );

        assertThrows(Exception.class, () -> {
            authenticationService.signUp(request);
        });
    }
}

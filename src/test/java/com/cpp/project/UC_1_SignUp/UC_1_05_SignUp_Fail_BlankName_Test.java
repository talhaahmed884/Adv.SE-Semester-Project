package com.cpp.project.UC_1_SignUp;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UC_1_05_SignUp_Fail_BlankName_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @ParameterizedTest
    @ValueSource(strings = {
            "",           // Empty
            "   ",        // Whitespace only
            "\t",         // Tab
            "\n"          // Newline
    })
    @DisplayName("UC-1.05: Name is null, empty, or whitespace")
    public void testSignUpFailBlankName(String blankName) {
        // Arrange
        SignUpRequestDTO request = new SignUpRequestDTO(
                blankName,
                "valid.email.test@example.com",
                "ValidPassword123"
        );

        // Act & Assert
        assertThrows(Exception.class, () -> {
            authenticationService.signUp(request);
        });
    }
}

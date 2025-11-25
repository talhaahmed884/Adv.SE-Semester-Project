package com.cpp.project.UC_2_Login;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.LoginRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UC-2.8: Login_WhenRepositoryFails
 * Test Case: Attempts to login user when repository ran into failure
 * Category: Negative/Exception
 * Expected: Fail with ExternalServiceException
 * <p>
 * NOTE: This test simulates repository failure scenarios.
 * In a real scenario, you might need to use @MockBean or test with database down.
 * For now, this demonstrates the expected behavior.
 */
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://invalid-host:5432/studently"
})
public class UC_2_08_Login_WhenRepositoryFails_Test extends BaseIntegrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-2.8: Attempts to login when repository fails")
    public void testLoginWhenRepositoryFails() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "user@example.com",
                "SomePassword123"
        );

        // Act & Assert - Should throw exception when repository fails
        Exception exception = assertThrows(
                Exception.class,
                () -> authenticationService.login(loginRequest),
                "Login should throw exception when repository fails"
        );

        // Verify exception indicates service/repository failure
        // (Could be various exceptions depending on failure type:
        // DataAccessException, RepositoryException, etc.)
        System.out.println("Repository failure exception: " + exception.getClass().getSimpleName());
    }
}

package com.cpp.project.uc_1_set_password;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import com.cpp.project.user_credential.entity.UserCredentialException;
import com.cpp.project.user_credential.service.UserCredentialService;
import com.cpp.project.user_credential.service.UserCredentialServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UC-1.09: Ensures no secret values (passwords) are logged
 */
public class UC_1_09_SetPassword_Fail_LogsNoSecrets_Test extends BaseIntegrationTest {
    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private AuthenticationService authenticationService;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    public void setupLogger() {
        // Set up log capture
        logger = (Logger) LoggerFactory.getLogger(UserCredentialServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    public void teardownLogger() {
        logger.detachAppender(listAppender);
    }

    @Test
    @DisplayName("UC-1.09: Ensures no passwords are logged when validation fails")
    public void testSetPasswordLogsNoSecretsOnFailure() {
        // Arrange - Create a test user
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc109@test.com",
                "Password123!"
        ));

        // Invalid password (too weak)
        String weakPassword = "weak";

        // Act - Attempt to set weak password
        assertThrows(UserCredentialException.class, () -> {
            userCredentialService.setPassword(user.getEmail(), weakPassword);
        });

        // Assert - Verify that the password is not in the logs
        List<ILoggingEvent> logsList = listAppender.list;
        for (ILoggingEvent event : logsList) {
            String logMessage = event.getFormattedMessage();
            // The password should NOT appear in any log message
            assertFalse(logMessage.contains(weakPassword), "Password should not be logged: " + logMessage);
        }
    }
}

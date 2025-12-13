package com.cpp.project.uc_16_logout_timer;

import com.cpp.project.authentication.service.AuthenticationService;
import com.cpp.project.entity.BaseIntegrationTest;
import com.cpp.project.timer.service.TimerService;
import com.cpp.project.user.dto.SignUpRequestDTO;
import com.cpp.project.user.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * UC-13.03: Logout with no active timers completes successfully
 */
public class UC_16_03_Logout_NoActiveTimers_Test extends BaseIntegrationTest {
    @Autowired
    private TimerService timerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("UC-13.03: Logout with no active timers succeeds")
    public void testLogoutNoActiveTimers() {
        // Arrange - Create user but don't start any timers
        UserDTO user = authenticationService.signUp(new SignUpRequestDTO(
                "Test User",
                "test.user.uc1303@test.com",
                "Password123!"
        ));

        // Act - Logout with no active timers (should not throw exception)
        assertDoesNotThrow(() -> timerService.stopAllActiveTimersForUser(user.getId()));

        // Assert - Method completes successfully without errors
        // No active timers to verify, just ensuring no exceptions were thrown
    }
}
